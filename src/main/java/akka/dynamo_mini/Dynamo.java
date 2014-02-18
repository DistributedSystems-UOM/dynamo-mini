package akka.dynamo_mini;

import akka.actor.*;
import akka.cluster.Cluster;
import akka.dynamo_mini.coordination.Bootstraper;
import akka.dynamo_mini.loadbalancer.LoadBalancer;
import akka.dynamo_mini.protocol.BootstraperProtocols.Test;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class Dynamo {
	
	public static ActorRef bootstraper;
	private static ActorSystem system;

    public static void main(String[] args) throws InterruptedException {
    	
        /**
         * Starting of Dynamo mini actors ans system setup
         */
        system = ActorSystem.create(systemName);
        Address joinAddress = Cluster.get(system).selfAddress();
        Thread.sleep(5000);
        startLoadBalancer(system, joinAddress);
        startDynamoRing(joinAddress);
      
        Thread.sleep(10000);
        System.out.println("Sending messages to nodes via boostraper ..");
        bootstraper.tell(new Test("Fuckers.1"),null);
        bootstraper.tell(new Test("Fuckers.2"),null);
        bootstraper.tell(new Test("Fuckers.3"),null);
        bootstraper.tell(new Test("Fuckers.4"),null);
        bootstraper.tell(new Test("Fuckers.5"),null);
        bootstraper.tell(new Test("Fuckers.6"),null);
        bootstraper.tell(new Test("Fuckers.7"),null);
        bootstraper.tell(new Test("Fuckers.8"),null);
        
    }

    private static String systemName = "Dynamo-mini";
    private static FiniteDuration workTimeout = Duration.create(10, "seconds");

    public static void startLoadBalancer(ActorSystem system, Address joinAddress) {
//        Config conf = ConfigFactory.parseString("akka.cluster.roles=[" + role + "]").
//                withFallback(ConfigFactory.load());
//        ActorSystem system = ActorSystem.create(systemName, conf);
//        Address realJoinAddress = (joinAddress == null) ? Cluster.get(system).selfAddress() : joinAddress;
        Cluster.get(system).join(joinAddress);
        system.actorOf(Props.create(LoadBalancer.class), "loadbalancer");
        /*system.actorOf(ClusterSingletonManager.defaultProps("active",
                PoisonPill.getInstance(), role , new ClusterSingletonPropsFactory() {
            public Props create(Object handOverData) {
                return LoadBalancer.props(workTimeout);
            }
        }), "loadbalancer");*/

//        return  realJoinAddress;
    }

    public static void startDynamoRing(Address joinAddress) throws InterruptedException{
//        system = ActorSystem.create(systemName);
        Cluster.get(system).join(joinAddress);

        bootstraper = system.actorOf(Props.create(Bootstraper.class), "bootstraper");
        Thread.sleep(5000);

        for(int i=1; i < 4 ; i++){ // Start Number of Virtual Nodes
            String nodeName = "node" + i;
            system.actorOf(Props.create(VirtualNode.class), nodeName);
        }
    }

}