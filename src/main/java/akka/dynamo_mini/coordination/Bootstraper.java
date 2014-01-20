package akka.dynamo_mini.coordination;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/14/14
 * Time: 10:51 PM
 * @email: gckarunarathne@gmail.com
 */
public class Bootstraper extends UntypedActor {

    // activate the extension
    ActorRef mediator =
            DistributedPubSubExtension.get(getContext().system()).mediator();

    @Override
    public void preStart() {

    }

    @Override
    public void postRestart(Throwable reason) {

    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof String) {
            String in = (String) msg;
            String out = in.toUpperCase();
            mediator.tell(new DistributedPubSubMediator.Publish("content", out), getSelf());
        } else {
            unhandled(msg);
        }
    }
}
