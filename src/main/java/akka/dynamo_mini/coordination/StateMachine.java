package akka.dynamo_mini.coordination;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dynamo_mini.persistence_engine.Persistence;
import akka.dynamo_mini.protocol.StateMachineProtocols.QuorumReadRequest;
import akka.dynamo_mini.protocol.StateMachineProtocols.QuorumWriteRequest;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.AckToWrite;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.GetKeyValue;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.PutKeyValue;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.ResultsValue;
import akka.event.Logging;
import akka.event.LoggingAdapter;

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
    private ArrayList prefList;
    private Persistence localDB;

    public StateMachine(ActorRef virtualNode, ArrayList preferencefList, Persistence localDB) {
        this.virtualNode = virtualNode;
        this.prefList = preferencefList;
        this.localDB = localDB;
    }

    @Override
    public void preStart() {

    }

    @Override
    public void postRestart(Throwable reason) {

    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof GetKeyValue) {
            GetKeyValue getKeyValue = (GetKeyValue) msg;
            this.sendResultsTo = getSender();
            virtualNode.tell(new QuorumReadRequest(getKeyValue.getKey()), getSelf());
        } else if (msg instanceof PutKeyValue) {
            PutKeyValue putKeyValue = (PutKeyValue) msg;
            this.sendResultsTo = getSender();
            virtualNode.tell(new QuorumWriteRequest(putKeyValue.getKey(), putKeyValue.getObjectValue()), getSelf());
        } else if (msg instanceof ResultsValue) {
            this.sendResultsTo.tell(msg, getSelf());
            getContext().stop(getSelf());
        } else if (msg instanceof AckToWrite) {
            AckToWrite ackToWrite = (AckToWrite) msg;
            if (ackToWrite.isSuccess())
                getContext().stop(getSelf());
            else {
                /**
                 * Retry again, writing.
                 */

            }
        } else {
            unhandled(msg);
        }
    }
}
