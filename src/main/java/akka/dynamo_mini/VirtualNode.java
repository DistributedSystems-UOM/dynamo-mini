package akka.dynamo_mini;

/**
 * Implementation of virtual node in Amazon Dynamo research paper.
 *
 * @author: Gihan Karunarathne
 * Date: 1/4/14
 * Time: 6:04 PM
 * @email: gckarunarathne@gmail.com
 */

import akka.actor.*;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberUp;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import akka.dynamo_mini.coordination.StateMachine;
import akka.dynamo_mini.node_management.ConsistentHash;
import akka.dynamo_mini.node_management.HashFunction;
import akka.dynamo_mini.persistence_engine.Memory;
import akka.dynamo_mini.persistence_engine.Persistence;
import akka.dynamo_mini.protocol.StateMachineProtocols.QuorumReadRequest;
import akka.dynamo_mini.protocol.StateMachineProtocols.QuorumWriteRequest;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.*;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.AckToWrite;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.GetKeyValue;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.PutKeyValue;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.ResultsValue;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.*;

import static akka.dynamo_mini.protocol.BootstraperProtocols.*;
import static akka.dynamo_mini.protocol.ClientProtocols.ReadRequest;
import static akka.dynamo_mini.protocol.ClientProtocols.WriteRequest;

public class VirtualNode extends UntypedActor {
    String nodeName = "";
    int numReplicas = Commons.numReplicas;
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    Cluster cluster = Cluster.get(getContext().system());
    ActorRef bootstraperRef, virtualNode;
    ActorSelection bootstraper;
    boolean isBootstraperUp = false;
    private ActorRef mediator = DistributedPubSubExtension.get(getContext().system()).mediator();
    private Persistence localDB;
    private int numOfNodes;

    /**
     * Store the preference list of other virtual nodes
     */
    ConsistentHash<ActorRef> ringManager;

    {
        // subscribe to the topic named "content"
        mediator.tell(new DistributedPubSubMediator.Subscribe("dynamo_mini_bootstraper", getSelf()), getSelf());
    }

    //subscribe to cluster changes, MemberUp
    @Override
    public void preStart() {
        cluster.subscribe(getSelf(), MemberUp.class);
        virtualNode = getSelf();
        nodeName = self().path().name();
        Address address = cluster.selfAddress();
        System.out.println("Virtual Node : " + nodeName + " is up @ " + address.protocol() + " : " + address.hostPort());
        this.bootstraper = getContext().actorSelection(address.protocol() + "://" + address.hostPort() + "/user/bootstraper");
        //this.bootstraper.tell(new Identify(nodeName), virtualNode);
        this.ringManager = new ConsistentHash<>(new HashFunction(), numReplicas, new ArrayList<ActorRef>());
        this.localDB = new Memory();
        this.numOfNodes = 0;
    }

    //re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public void onReceive(Object msg) {
        /*****************************************************
         * Read write requests form the state machines
         *****************************************************/
        if (msg instanceof QuorumReadRequest) {
            QuorumReadRequest quorumReadRequest = (QuorumReadRequest) msg;
            getSender().tell(new ResultsValue(localDB.get(quorumReadRequest.getKey()), null), getSelf());
        } else if (msg instanceof QuorumWriteRequest) {
            QuorumWriteRequest quorumWriteRequest = (QuorumWriteRequest) msg;
            getSender().tell(new AckToWrite(localDB.put(quorumWriteRequest.getKey(), quorumWriteRequest.getObject()), null), getSelf());
        }
        /*****************************************************
         * Client Requests to the Coordinator
         *****************************************************/
        else if (msg instanceof ReadRequest) { // most frequent request
            ReadRequest readRequest = (ReadRequest) msg;
            log.info("Read Request -- ( " + readRequest.getKey() + ")");
            /**
             * If this is the node responsible for handling the request, then process.
             * Otherwise forward to the relevant node (coordinator).
             */
            log.info("## " + nodeName + " - Read Request : Key = " + readRequest.getKey());
            ActorRef coordinator = ringManager.get(readRequest.getKey());
            if (coordinator.path().name().equals(virtualNode.path().name())) {
                ActorRef stateMachine = getContext().actorOf(Props.create(StateMachine.class, getSelf(),
                        ringManager.getPreferenceList(readRequest.getKey())));
                /**
                 * Send Preference List to the State machine
                 */

                /* Testing preference lists
                ArrayList<ActorRef> refs = ringManager.getPreviousList(readRequest.getKey());
                System.out.println("Printing previous list");
                for (ActorRef ref : refs) {
                    System.out.println(ref);
                }
                refs = ringManager.getPreferenceList(readRequest.getKey());
                System.out.println("Printing preference list");
                for (ActorRef ref : refs) {
                    System.out.println(ref);
                }*/

                stateMachine.forward(new GetKeyValue(readRequest.getKey()), getContext());
            } else {
                log.info("Forward write request to :" + nodeName + " from " + coordinator.path());
                coordinator.forward(msg, getContext());
            }
        } else if (msg instanceof WriteRequest) {
            WriteRequest writeRequest = (WriteRequest) msg;
            log.info("Write Request -- ( " + writeRequest.getKey() + "," + writeRequest.getObjectValue() + " )");
            /**
             * If this is the node responsible for handling the request, then process.
             * Otherwise forward to the relevant node (coordinator).
             */
            ActorRef coordinator = ringManager.get(writeRequest.getKey());
            if (coordinator.path().name().equals(virtualNode.path().name())) {
                ActorRef stateMachine = getContext().actorOf(Props.create(StateMachine.class, getSelf(),
                        ringManager.getPreferenceList(writeRequest.getKey())));
                /**
                 * Send Preference List to the State machine
                 */
                stateMachine.forward(new PutKeyValue(writeRequest.getKey(), null, writeRequest.getObjectValue()), getContext());
            } else {
                log.info("Forward write request to :" + nodeName + " from " + coordinator.path());
                coordinator.forward(msg, getContext());
            }
        }
        /*****************************************************
         * Virtual Node steps
         *****************************************************/
        else if (msg instanceof MoveDataToNewNode) {
            MoveDataToNewNode moveDataToNewNode = (MoveDataToNewNode) msg;
            localDB.pasteData(moveDataToNewNode.getData());
        }
        /**************************************************
         * Bootstrap steps
         **************************************************/
        else if (msg instanceof DistributedPubSubMediator.SubscribeAck) {
            //log.info("subscribing " + nodeName);
            // Bootstraper is running. Ask to send join to ring to current virtual nodes in the ring.
            //log.info("Ask for join to the ring from bootstraper");
            /** Contact boostraper and ask for join into the ring. */
            this.bootstraper.tell(new JoinToRing(nodeName), getSelf());
        }
        /**
         * Details about a new node which is broadcast by the bootstraper.
         */
        else if (msg instanceof AddNewNodeToRing) {
            AddNewNodeToRing addNewNodeToRing = (AddNewNodeToRing) msg;
            if (getSelf() != addNewNodeToRing.getActorRef()) {
                ArrayList prefList = ringManager.getPreferenceList(addNewNodeToRing.getActorRef().path().name());
                if (this.isContainInPrefList(getSelf().path().name(), prefList)) {
                    this.addNewNodeHandle(addNewNodeToRing.getActorRef(),prefList,
                            ringManager.getPreferenceList(addNewNodeToRing.getActorRef().path().name()));
                    /** Should add to the ring after sending data */
                    ringManager.add(addNewNodeToRing.getActorRef());
                    /**  Send own details to the new node  */
                    addNewNodeToRing.getActorRef().tell(new CurrentRingNode(nodeName, getSelf()), getSelf());
                    this.numOfNodes++;
                }
            }
        }
        /**
         * ACK for previous request to join into the ring.
         */
        else if (msg instanceof ACKJoinToRing) {
            ACKJoinToRing ackJoinToRing = (ACKJoinToRing) msg;
            //ackJoinToRing.getNumNodes();
            //log.info(nodeName + " got ACK from bootstraper");
            ringManager.add(getSelf());
            this.numOfNodes++;
        }
        /**
         * Details of a virtual node which is already in the ring.
         */
        else if (msg instanceof CurrentRingNode) {
            CurrentRingNode currentRingNode = (CurrentRingNode) msg;
            log.info("Add node " + currentRingNode.getActorRef().path().name() + " to the ring in " + nodeName + " /" + this.numOfNodes);
            ringManager.add(currentRingNode.getActorRef());
            this.numOfNodes++;
        }
        /**
         *
         */
        else if (msg instanceof ActorIdentity) {
            ActorIdentity identity = (ActorIdentity) msg;
            log.info("Bootstraper replied with :" + identity.correlationId() + " to node:" + nodeName);
            if (identity.correlationId().equals(nodeName)) {
                /** Not Working. Need to check.
                 Res: http://doc.akka.io/docs/akka/snapshot/java/untyped-actors.html#actorselection-java
                 */
                ActorRef ref = identity.getRef();
                if (ref == null) {
                    getContext().stop(getSelf());
                } else {
                    bootstraperRef = ref;
                    getContext().watch(bootstraperRef);
                    log.info(nodeName + " send ACK to Bootstraper: " + ref.path().name());
                    virtualNode.tell("ACK by " + nodeName, getSelf());
                }
                isBootstraperUp = true;
            }

        } else if (msg instanceof Terminated) {
            final Terminated t = (Terminated) msg;
            ActorRef actor = t.getActor();
            log.info("Actor: " + actor.path() + " terminated. Detect by VN: " + nodeName);
            if (t.getActor().equals(bootstraperRef)) {
                getContext().stop(getSelf());
            }

        } else if (msg instanceof Test) {

            Test data = (Test) msg;

            System.out.println("Actor : " + nodeName + " message: " + data.getMsg());

        } else {
            unhandled(msg);
        }

        /**else if (msg.equals(BACKEND_REGISTRATION)) {
         getContext().watch(getSender());
         backends.add(getSender());

         } else if (msg instanceof Terminated) {
         Terminated terminated = (Terminated) msg;
         backends.remove(terminated.getActor());

         } else {
         unhandled(msg);
         }*/
    }

    private void addNewNodeHandle(ActorRef newNode, ArrayList<ActorRef> prefList, ArrayList<ActorRef> prevList) {

        System.out.println(nodeName + " - *************************************************************** " + prefList.size());
        for (ActorRef actorRef : prefList) {
            System.out.println(">>>> " + nodeName + " : " + actorRef + " when adding - " + newNode);
        }
        if(ringManager.getSize() <= numReplicas)
            return;

        // Reverse the previous nodes list
        Collections.reverse(prevList);
        prevList.add(newNode);
        for (int i = 0; i < prefList.size(); i++){
            if (getSelf().path().name().equals(prefList.get(i).path().name())){
                System.out.println("pref : " + prefList.size() + " prev: " + prevList.size());
                System.out.println("Move data in range :" + prevList.get(i).path().name() + " to:" + prevList.get(i + 1).path().name());
                SortedMap data = (SortedMap) localDB.copyData(prevList.get(i).path().name(), prevList.get(i + 1).path().name());
                newNode.tell(new MoveDataToNewNode(data),getSelf());
                localDB.deleteData(prevList.get(i).path().name(), prevList.get(i + 1).path().name());
            }
        }
    }

    private void removeNewNodeHandle(ActorRef rmNode, ArrayList<ActorRef> prefList, ArrayList<ActorRef> prevList) {
        if(ringManager.getSize() <= numReplicas)
            return;

        // Reverse the previous nodes list
        Collections.reverse(prevList);
        prevList.add(rmNode);
        for (int i = 0; i < prefList.size(); i++){
            if (getSelf().path().name().equals(prefList.get(i).path().name())){
                System.out.println("pref : " + prefList.size() + " prev: " + prevList.size());
                System.out.println("Move data in range :" + prevList.get(i).path().name() + " to:" + prevList.get(i + 1).path().name());
                SortedMap data = (SortedMap) localDB.copyData(prevList.get(i).path().name(), prevList.get(i + 1).path().name());
                rmNode.tell(new MoveDataToNewNode(data),getSelf());
                localDB.deleteData(prevList.get(i).path().name(), prevList.get(i + 1).path().name());
            }
        }
    }

    private boolean isContainInPrefList(String name, ArrayList<ActorRef> prefList) {
        if (prefList == null) return false;

        for (ActorRef ref : prefList) {
            if (ref.path().name().equals(name))
                return true;
        }
        return false;
    }

}
