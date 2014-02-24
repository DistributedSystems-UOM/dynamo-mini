package dynamo_server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.dynamo_mini.Dynamo;
import akka.dynamo_mini.VirtualNode;
import akka.dynamo_mini.clienthandle.DynamoClient;
import akka.dynamo_mini.coordination.Bootstraper;
import akka.dynamo_mini.loadbalancer.LoadBalancer;

public class DynamoStarter implements ServletContextListener {

    Dynamo uomDynamo = Dynamo.INSTANCE;

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {

        try {
            uomDynamo.start();
            Thread.sleep(1000);
            uomDynamo.startLoadBalancer();
            uomDynamo.startDynamoRing();
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
