package akka.dynamo_mini.coordination;

import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.concurrent.duration.FiniteDuration;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/14/14
 * Time: 10:51 PM
 * @email: gckarunarathne@gmail.com
 */
public class Bootstraper extends UntypedActor{

    public static Props props(FiniteDuration workTimeout) {
        return Props.create(Bootstraper.class, workTimeout);
    }

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
