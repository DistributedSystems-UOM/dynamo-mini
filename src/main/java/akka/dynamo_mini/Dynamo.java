package akka.dynamo_mini;

import akka.actor.*;
import akka.cluster.Cluster;
import akka.dynamo_mini.clienthandle.DynamoClient;
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
        Thread.sleep(1000);
        startLoadBalancer(system, joinAddress);
        startDynamoRing(joinAddress);
        Thread.sleep(1000);
        createClient(system);

    }

    private static String systemName = "Dynamo-mini";
    private static FiniteDuration workTimeout = Duration.create(10, "seconds");

    public static void startLoadBalancer(ActorSystem system, Address joinAddress) {
        Cluster.get(system).join(joinAddress);
        system.actorOf(Props.create(LoadBalancer.class), "loadbalancer");
    }

    public static void startDynamoRing(Address joinAddress) throws InterruptedException{
        Cluster.get(system).join(joinAddress);

        bootstraper = system.actorOf(Props.create(Bootstraper.class), "bootstraper");
        Thread.sleep(1000);

        for(int i=1; i < 8 ; i++){ // Start Number of Virtual Nodes
            String nodeName = "node" + i;
            system.actorOf(Props.create(VirtualNode.class), nodeName);
            Thread.sleep(1000);
        }
    }
    
    public static void createClient(ActorSystem system){
        system.actorOf(Props.create(DynamoClient.class), "client1");
    }
}