package akka.dynamo_mini;

import akka.actor.*;
import akka.cluster.Cluster;
import akka.dynamo_mini.coordination.Bootstraper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class Dynamo {

    public static void main(String[] args) throws InterruptedException {
        /**
         * Starting of Dynamo mini actors ans system setup
         */
    	Config conf = ConfigFactory.parseString("akka.cluster.roles=[ring]").withFallback(ConfigFactory.load());
    	ActorSystem system = ActorSystem.create(systemName, conf);
        Address joinAddress = Cluster.get(system).selfAddress();
        Thread.sleep(5000);
       startDynamoRing(joinAddress);
        
    }

    private static String systemName = "Dynamo-mini";
    private static FiniteDuration workTimeout = Duration.create(10, "seconds");
    private static ActorRef bootstraper = null;

   
    public static void startDynamoRing(Address joinAddress) throws InterruptedException{
        
    	ActorSystem system = ActorSystem.create(systemName);
        Cluster.get(system).join(joinAddress);
        bootstraper =system.actorOf(Props.create(Bootstraper.class), "bootstraper");
        
        Thread.sleep(5000);
        System.out.println("Creating nodes");
        for(int i=1; i < 4 ; i++){ // Start Number of Virtual Nodes
            String nodeName = "node" + i;
            system.actorOf(Props.create(VirtualNode.class), nodeName);
        }
    }

}
