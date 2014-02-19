package akka.dynamo_mini.coordination;

import akka.actor.*;
import akka.cluster.Cluster;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import akka.dynamo_mini.Commons;
import akka.dynamo_mini.node_management.HashFunction;
import akka.dynamo_mini.protocol.BootstraperProtocols.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.RoundRobinRouter;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * According to the section 4.8.2 - External Discovery in Amazon Dynamo paper, when new node wants to the
 * ring and get to know about other nodes in the ring, it needs to have separate set of nodes which are known to
 * external parties.
 * In Dynamo mini, we are using separate set of actors who are playing the role of seeds for the system.
 * The paper propose two types methods to obtains the seeds;
 * 1. from a static configuration
 * 2. from a configuration service
 *
 * @author: Gihan Karunarathne
 * Date: 1/14/14
 * Time: 10:51 PM
 * @email: gckarunarathne@gmail.com
 */

public class Bootstraper extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private SortedMap<Integer,ActorRef> currRingNodes;
    private static String systemName = "Dynamo-mini";
    private ActorRef router;
    private final HashFunction hashFunction = new HashFunction();

    Cluster cluster = Cluster.get(getContext().system());
    Address address = cluster.selfAddress();
    private ActorSelection loadbalancer = getContext().actorSelection(
            address.protocol() + "://" + address.hostPort() + "/user/loadbalancer");

    final int numReplicas = Commons.numReplicas;

    // activate the extension
    ActorRef mediator = DistributedPubSubExtension.get(getContext().system()).mediator();

    @Override
    public void preStart() {
        this.currRingNodes = new TreeMap<>();
    }

    @Override
    public void postRestart(Throwable reason) {

    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof JoinToRing) {
            JoinToRing joinToRing = (JoinToRing) msg;

            mediator.tell(new DistributedPubSubMediator.Publish("dynamo_mini_bootstraper",
                    new AddNewNodeToRing(joinToRing.getNodeName(), getSender())), getSelf());
            getSender().tell(new ACKJoinToRing(joinToRing.getNodeName(), currRingNodes.size()), getSelf());
            loadbalancer.tell(new LBUpdateAdd(getSender()), getSelf());
            this.currRingNodes.put(hashFunction.hash(getSender().toString()),getSender());
        } else if (msg instanceof LeaveRing) {
            LeaveRing leaveRing = (LeaveRing) msg;
            //Code to remove the node from dynamo ring
            this.currRingNodes.remove(getSender());
            loadbalancer.tell(new LBUpdateRemove(getSender()), getSelf());
        } else if (msg instanceof Test) {
            log.info("Bootstraper got the message");
            router.tell(msg, getSender());
        } else if (msg instanceof NewNodeConnected) {
            NewNodeConnected nnc = (NewNodeConnected) msg;
            //routees.add(getSender());
            //reinitiateRouter();
            //System.out.println("new node connected.. to Bootstraper");
        } else {
            unhandled(msg);
        }
    }

    private void reinitiateRouter() {
        ActorSystem system = ActorSystem.create(systemName);
        router = system.actorOf(Props.empty().withRouter(RoundRobinRouter.create(new ArrayList<ActorRef>())));

    }
}
