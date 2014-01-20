package akka.dynamo_mini;

import akka.actor.*;
import akka.cluster.Cluster;
import akka.contrib.pattern.ClusterSingletonManager;
import akka.contrib.pattern.ClusterSingletonPropsFactory;
import akka.dynamo_mini.coordination.Bootstraper;
import akka.dynamo_mini.loadbalancer.LoadBalancer;
import akka.dynamo_mini.node_management.ConsistentHash;
import akka.dynamo_mini.node_management.HashFunction;
import akka.dynamo_mini.node_management.TestActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;

public class Dynamo {

    public static void main(String[] args) throws InterruptedException {
        /**
         * Starting of Dynamo mini actors ans system setup
         */
        Address joinAddress = startLoadBalancer(null, "loadbalancer");
        Thread.sleep(5000);
        // startLoadBalancer(joinAddress, "loadbalancer");
        startBootstraps(joinAddress);
        Thread.sleep(5000);
        startVirtualNodes(joinAddress);

        /*ActorSystem system = ActorSystem.create("Dynamo-mini");
        ActorRef node1 = system.actorOf(Props.create(TestActor.class), "node1");
        ActorRef node2 = system.actorOf(Props.create(TestActor.class), "node2");
        ArrayList<ActorRef> nodeList = new ArrayList<ActorRef>();
        nodeList.add(node1);
        nodeList.add(node2);
        HashFunction hashFunction = new HashFunction();
        ConsistentHash<ActorRef> nodeManager = new ConsistentHash<>(hashFunction, 1, nodeList);
        System.out.println("'KeyTest1' is storing in "+nodeManager.get("KeyTest1").toString()+" of the ring.");
        System.out.println("'KeyTest2' is storing in "+nodeManager.get("KeyTest2").toString()+" of the ring.");
        System.out.println("Done ###");*/
        
    }

    private static String systemName = "Dynamo-mini";
    private static FiniteDuration workTimeout = Duration.create(10, "seconds");

    public static Address startLoadBalancer(Address joinAddress, String role) {
        Config conf = ConfigFactory.parseString("akka.cluster.roles=[" + role + "]").
                withFallback(ConfigFactory.load());
        ActorSystem system = ActorSystem.create(systemName, conf);
        Address realJoinAddress = (joinAddress == null) ? Cluster.get(system).selfAddress() : joinAddress;
        Cluster.get(system).join(realJoinAddress);

        /*system.actorOf(ClusterSingletonManager.defaultProps("active",
                PoisonPill.getInstance(), role , new ClusterSingletonPropsFactory() {
            public Props create(Object handOverData) {
                return LoadBalancer.props(workTimeout);
            }
        }), "loadbalancer");*/

        return  realJoinAddress;
    }

    public static void startBootstraps(Address joinAddress) {
        ActorSystem system = ActorSystem.create(systemName);
        Cluster.get(system).join(joinAddress);

        ActorRef publisher = system.actorOf(Props.create(Bootstraper.class), "bootstraper");
    }

    public static void startVirtualNodes(Address joinAddress) {
        ActorSystem system = ActorSystem.create(systemName);
        Cluster.get(system).join(joinAddress);
        for(int i=1; i < 11 ; i++){ // Start Number of Virtual Nodes
            String nodeName = "node" + i;
            system.actorOf(Props.create(VirtualNode.class), nodeName);
        }
    }

}
