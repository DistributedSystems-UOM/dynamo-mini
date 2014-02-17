package akka.dynamo_mini.coordination;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

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

    @Override
    public void preStart() {

    }

    @Override
    public void postRestart(Throwable reason) {

    }

    @Override
    public void onReceive(Object o) throws Exception {

    }

}
