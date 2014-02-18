package akka.dynamo_mini.clienthandle;

import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.dynamo_mini.protocol.ClientProtocols.WriteRequest;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.PutKeyValue;

public class DynamoClient extends UntypedActor {

    Cluster cluster = Cluster.get(getContext().system());
    Address address = cluster.selfAddress();
    ActorSelection loadbalancer = getContext().actorSelection(
            address.protocol() + "://" + address.hostPort() + "/user/loadbalancer");

    @Override
    public void preStart() {
        WriteRequest writeRequest1 = new WriteRequest("Put:Key-1", null, "Put:Object-1");
        WriteRequest writeRequest2 = new WriteRequest("Put:Key-2", null, "Put:Object-2");
        WriteRequest writeRequest3 = new WriteRequest("Put:Key-3", null, "Put:Object-3");
        WriteRequest writeRequest4 = new WriteRequest("Put:Key-4", null, "Put:Object-4");
        WriteRequest writeRequest5 = new WriteRequest("Put:Key-5", null, "Put:Object-5");
        WriteRequest writeRequest6 = new WriteRequest("Put:Key-6", null, "Put:Object-6");
        loadbalancer.tell(writeRequest1, getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof String) {
            System.out.println("Dynamo Client : " + message);
        } else {
            unhandled(message);
        }
    }

}