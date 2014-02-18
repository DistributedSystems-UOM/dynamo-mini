package akka.dynamo_mini.coordination;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.dynamo_mini.protocol.StateMachineProtocols.*;
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

    private ActorRef virtualNode;
    private ArrayList prefList;
    public StateMachine(ActorRef vn,ArrayList prerencefList){
        virtualNode = vn;
        prefList = prerencefList;
    }

    @Override
    public void preStart() {

    }

    @Override
    public void postRestart(Throwable reason) {

    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof QuorumReadRequest) {
             QuorumReadRequest readRequest = (QuorumReadRequest) msg;

        } else if (msg instanceof QuorumWriteRequest) {


        }
    }
}
