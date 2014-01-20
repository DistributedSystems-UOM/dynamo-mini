package akka.dynamo_mini.coordination;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import akka.dynamo_mini.protocol.BootstraperProtocols.ACKJoinToRing;
import akka.dynamo_mini.protocol.BootstraperProtocols.AddNewNodeToRing;
import akka.dynamo_mini.protocol.BootstraperProtocols.JoinToRing;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/14/14
 * Time: 10:51 PM
 * @email: gckarunarathne@gmail.com
 */
public class Bootstraper extends UntypedActor {
    final int numReplicas = 1;
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    // activate the extension
    ActorRef mediator = DistributedPubSubExtension.get(getContext().system()).mediator();

    @Override
    public void preStart() {
        log.info("Bootstraper Starting...");
    }

    @Override
    public void postRestart(Throwable reason) {

    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof JoinToRing) {
            JoinToRing joinToRing = (JoinToRing) msg;
            log.info("SendAll() about new node subscription of " + joinToRing.getNodeName());
            mediator.tell(new DistributedPubSubMediator.Publish("dynamo_mini_bootstraper",
                    new AddNewNodeToRing(joinToRing.getNodeName(), getSender())), getSelf());
            getSender().tell(new ACKJoinToRing(joinToRing.getNodeName(), numReplicas), getSelf());
        } else if (msg instanceof String) {
            log.info("Bootstraper " + (String) msg);
        } else {
            System.out.println("Bootstrap Unhandled Message");
            unhandled(msg);
        }
    }
}
