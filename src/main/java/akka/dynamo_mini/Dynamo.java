package akka.dynamo_mini;

import java.util.ArrayList;

import akka.actor.*;
import akka.cluster.Cluster;
import akka.dynamo_mini.clienthandle.DynamoClient;
import akka.dynamo_mini.coordination.Bootstraper;
import akka.dynamo_mini.loadbalancer.LoadBalancer;
import akka.dynamo_mini.protocol.BootstraperProtocols.Test;
import akka.dynamo_mini.protocol.ClientProtocols.ReadRequest;
import akka.dynamo_mini.protocol.ClientProtocols.WriteRequest;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public enum Dynamo {
    INSTANCE;
    public ActorRef bootstraper;
    public ActorSystem system;
    public Address joinAddress;
    ArrayList<ActorRef> clientList = new ArrayList<ActorRef>();
    
    // public static void main(String[] args) throws InterruptedException {
    //
    // /**
    // * Starting of Dynamo mini actors ans system setup
    // */
    // system = ActorSystem.create(systemName);
    // Address joinAddress = Cluster.get(system).selfAddress();
    // Thread.sleep(1000);
    // startLoadBalancer(system, joinAddress);
    // startDynamoRing(joinAddress);
    // Thread.sleep(1000);
    // createClient(system);
    //
    // }

    private static String systemName = "Dynamo-mini";
    private static FiniteDuration workTimeout = Duration.create(10, "seconds");

    public void start() {
        system = ActorSystem.create(systemName);
        joinAddress = Cluster.get(system).selfAddress();
    }

    public void startLoadBalancer() {
        Cluster.get(system).join(joinAddress);
        system.actorOf(Props.create(LoadBalancer.class), "loadbalancer");
    }

    public void startDynamoRing() throws InterruptedException {
        Cluster.get(system).join(joinAddress);

        bootstraper = system.actorOf(Props.create(Bootstraper.class), "bootstraper");
        Thread.sleep(1000);

        for (int i = 1; i < 8; i++) { // Start Number of Virtual Nodes
            String nodeName = "node" + i;
            system.actorOf(Props.create(VirtualNode.class), nodeName);
            Thread.sleep(1000);
        }
    }

    public void createClient() {
        system.actorOf(Props.create(DynamoClient.class), "client1");
    }
    
    public void createClient(WriteRequest writeReq){
        clientList.add(system.actorOf(Props.create(DynamoClient.class), "client"+clientList.size()+1));
        clientList.get(clientList.size()-1).tell(writeReq, clientList.get(clientList.size()-1));
    }
    
    public void createClient(ReadRequest readReq){
        clientList.add(system.actorOf(Props.create(DynamoClient.class), "client"+clientList.size()+1));
        clientList.get(clientList.size()-1).tell(readReq, clientList.get(clientList.size()-1));
    }
}