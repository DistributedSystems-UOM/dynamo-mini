package akka.dynamo_mini.coordination;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.VectorClock;
import akka.dynamo_mini.Commons;
import akka.dynamo_mini.protocol.StateMachineProtocols.QuorumReadRequest;
import akka.dynamo_mini.protocol.StateMachineProtocols.QuorumWriteRequest;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.AckToWrite;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.GetKeyValue;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.PutKeyValue;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.ResultsValue;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.collection.immutable.TreeMap;

import java.util.ArrayList;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/12/14
 * Time: 12:45 AM
 * @email: gckarunarathne@gmail.com
 */
public class StateMachine extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private ActorRef virtualNode, sendResultsTo;
    private ArrayList<ActorRef> prefList;
    private int numOfReplicas = Commons.numReplicas, respondR, respondW;

    public StateMachine(ActorRef virtualNode, ArrayList<ActorRef> preferencefList) {
        this.virtualNode = virtualNode;
        this.prefList = preferencefList;
    }

    @Override
    public void preStart() {
        this.respondR = 0;
        this.respondW = 0;
    }

    @Override
    public void postRestart(Throwable reason) {

    }

    @Override
    public void onReceive(Object msg) throws Exception {
        /**
         * After a virtual node create a state machine for READ request,
         * send a this type of message.
         */
        if (msg instanceof GetKeyValue) {
            GetKeyValue getKeyValue = (GetKeyValue) msg;
            this.sendResultsTo = getSender();
            for (ActorRef actorRef : prefList) {
                // System.out.println(actorRef.toString() + " current: " + getSelf().toString());
                actorRef.tell(new QuorumReadRequest(getKeyValue.getKey()), getSelf());
            }
        }
        /**
         * After a virtual node create a state machine for WRITE request,
         * send a this type of message.
         */
        else if (msg instanceof PutKeyValue) {
            PutKeyValue putKeyValue = (PutKeyValue) msg;

            this.sendResultsTo = getSender();
            for (ActorRef actorRef : prefList) {
                actorRef.tell(new QuorumWriteRequest(putKeyValue.getKey(), putKeyValue.getObjectValue()), getSelf());
            }
        }
        /**
         * Results for a read request.
         */
        else if (msg instanceof ResultsValue) {
            if (this.respondR++ == Commons.R) {
                this.sendResultsTo.tell(msg, getSelf());
                getContext().stop(getSelf());
            }
        }
        /**
         * Status/ACK after successful write.
         */
        else if (msg instanceof AckToWrite) {
            AckToWrite ackToWrite = (AckToWrite) msg;
            if (ackToWrite.isSuccess()) {
                if (this.respondR++ == Commons.R) {
                    log.info("Write is successful");
                    getContext().stop(getSelf());
                }
            } else {
                /**
                 * Retry again, writing.
                 */
                getContext().stop(getSelf());
            }
        } else {
            unhandled(msg);
        }
    }
}
