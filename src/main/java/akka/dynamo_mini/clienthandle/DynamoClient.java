package akka.dynamo_mini.clienthandle;

import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.PutKeyValue;

public class DynamoClient extends UntypedActor{
    
    Cluster cluster = Cluster.get(getContext().system());
    Address address = cluster.selfAddress();
    ActorSelection loadbalancer = getContext().actorSelection(address.protocol() + "://" +address.hostPort() + "/user/loadbalancer");
    
    @Override
    public void preStart() {
        PutKeyValue putReq1 = new PutKeyValue("Put:Key-1", null, "Put:Object-1");
        PutKeyValue putReq2 = new PutKeyValue("Put:Key-2", null, "Put:Object-2");
        PutKeyValue putReq3 = new PutKeyValue("Put:Key-3", null, "Put:Object-3");
        PutKeyValue putReq4 = new PutKeyValue("Put:Key-4", null, "Put:Object-4");
        PutKeyValue putReq5 = new PutKeyValue("Put:Key-5", null, "Put:Object-5");
        PutKeyValue putReq6 = new PutKeyValue("Put:Key-6", null, "Put:Object-6");
        loadbalancer.tell(putReq1,getSelf());
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof String){
            System.out.println("Dynamo Client : "+ message);
        }
        else{
            unhandled(message);
        }
    }

}
