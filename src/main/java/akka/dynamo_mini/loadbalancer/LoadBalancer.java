package akka.dynamo_mini.loadbalancer;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.cluster.ClusterEvent.MemberUp;
import akka.event.Logging;
import akka.event.LoggingAdapter;
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

	private List<ActorRef> ringMembers = new ArrayList<ActorRef>();
	
	@Override
	public void onReceive(Object message) throws Exception {
		// TODO Auto-generated method stub
		
		if (message instanceof MemberUp) {

		      ringMembers.add(getSender());
		 
		 } else {
		      unhandled(message);
		 }
		
	}
	
	
	
	
	
	
   
}
