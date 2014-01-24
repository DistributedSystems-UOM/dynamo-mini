package akka.dynamo_mini.coordination;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Identify;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import akka.dynamo_mini.protocol.BootstraperProtocols.ACKJoinToRing;
import akka.dynamo_mini.protocol.BootstraperProtocols.AddNewNodeToRing;
import akka.dynamo_mini.protocol.BootstraperProtocols.JoinToRing;
import akka.dynamo_mini.protocol.BootstraperProtocols.NewNodeConnected;
import akka.dynamo_mini.protocol.BootstraperProtocols.Test;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.RoundRobinRouter;
import akka.routing.RouterRoutees;
import akka.routing.SmallestMailboxRouter;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/14/14
 * Time: 10:51 PM
 * @email: gckarunarathne@gmail.com
 */

//This is a custom router class 

public class Bootstraper extends UntypedActor {
	
	
	List<ActorRef> routees = new ArrayList<ActorRef>();
	private static String systemName = "Dynamo-mini";
	ActorRef router;
	
    final int numReplicas = 1;
    
    // activate the extension
    ActorRef mediator = DistributedPubSubExtension.get(getContext().system()).mediator();

    @Override
    public void preStart() {
    	

        
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
            getSender().tell(new ACKJoinToRing(joinToRing.getNodeName(), numReplicas), getSelf());
            
        } else if (msg instanceof Test) {
        	
            System.out.println("Bootstraper got the message");
        	router.tell(msg, getSender());
            
        }else if (msg instanceof NewNodeConnected){
        	
        	NewNodeConnected nnc = (NewNodeConnected) msg;
        	routees.add(getSender());
        	reinitiateRouter();
        	System.out.println("new node connected.. to Bootstraper");
    	
        }
        else {
            
            unhandled(msg);
        }
    }
    
   
    private void reinitiateRouter(){
    	
    	ActorSystem system = ActorSystem.create(systemName);
    	router = system.actorOf(Props.empty().withRouter(RoundRobinRouter.create(routees)));
    	
    }
}