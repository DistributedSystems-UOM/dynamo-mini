package akka.dynamo_mini.loadbalancer;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dynamo_mini.protocol.GetReq;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/15/14
 * Time: 12:35 AM
 * @email: gckarunarathne@gmail.com
 * Auto generate get(), put() calls from dynamo-mini.
 * Testing purpose.
 */
public class LoadBalancer extends UntypedActor{

    public static Props props(FiniteDuration workTimeout) {
        return Props.create(LoadBalancer.class, workTimeout);
    }

    public static Props props(ActorRef clusterClient, Props workExecutorProps, FiniteDuration registerInterval) {
        return Props.create(LoadBalancer.class, clusterClient, workExecutorProps, registerInterval);
    }

    public static Props props(ActorRef clusterClient, Props workExecutorProps) {
        return props(clusterClient, workExecutorProps, Duration.create(10, "seconds"));
    }

    private final FiniteDuration workTimeout;
    
    public LoadBalancer(FiniteDuration workTimeout) {
        this.workTimeout = workTimeout;
      }
    
    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof GetReq){
            GetReq getReq = (GetReq)message;
            System.out.println(getReq.key);
        }
    }
}
