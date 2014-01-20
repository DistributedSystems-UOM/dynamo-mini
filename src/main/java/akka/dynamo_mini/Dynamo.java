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
        Address joinAddress = startLoadBalancer(null, "loadbalancer");
        Thread.sleep(5000);
        // startLoadBalancer(joinAddress, "loadbalancer");
        startDynamoRing(joinAddress);
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

    public static void startDynamoRing(Address joinAddress) throws InterruptedException{
        ActorSystem system = ActorSystem.create(systemName);
        Cluster.get(system).join(joinAddress);

        system.actorOf(Props.create(Bootstraper.class), "bootstraper");
        Thread.sleep(5000);

        for(int i=1; i < 4 ; i++){ // Start Number of Virtual Nodes
            String nodeName = "node" + i;
            system.actorOf(Props.create(VirtualNode.class), nodeName);
        }
    }

}
