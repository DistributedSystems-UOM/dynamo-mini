package akka.dynamo_mini.clienthandle;

import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.dynamo_mini.protocol.ClientProtocols.ReadRequest;
import akka.dynamo_mini.protocol.ClientProtocols.WriteRequest;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.ResultsValue;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class DynamoClient extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    Cluster cluster = Cluster.get(getContext().system());
    Address address = cluster.selfAddress();
    ActorSelection loadbalancer = getContext().actorSelection(
            address.protocol() + "://" + address.hostPort() + "/user/loadbalancer");

    @Override
    public void preStart() throws Exception {
        WriteRequest writeRequest1 = new WriteRequest("Put:Key-1", null, "Put:Object-1");
        WriteRequest writeRequest2 = new WriteRequest("Put:Key-2", null, "Put:Object-2");
        WriteRequest writeRequest3 = new WriteRequest("Put:Key-3", null, "Put:Object-3");
        WriteRequest writeRequest4 = new WriteRequest("Put:Key-4", null, "Put:Object-4");
        WriteRequest writeRequest5 = new WriteRequest("Put:Key-5", null, "Put:Object-5");
        WriteRequest writeRequest6 = new WriteRequest("Put:Key-6", null, "Put:Object-6");
        loadbalancer.tell(writeRequest1, getSelf());
        loadbalancer.tell(writeRequest2, getSelf());
        loadbalancer.tell(writeRequest3, getSelf());
        loadbalancer.tell(writeRequest4, getSelf());
        loadbalancer.tell(writeRequest5, getSelf());
        loadbalancer.tell(writeRequest6, getSelf());
        Thread.sleep(5000);
        ReadRequest readRequest1 = new ReadRequest("Put:Key-1");
        loadbalancer.tell(readRequest1, getSelf());
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof ResultsValue) {
            ResultsValue readRequest = (ResultsValue) msg;
            log.info("Get results as " + readRequest.getNewObject());
        } else if (msg instanceof KeyVal) {
            KeyVal req = (KeyVal) msg;
            log.info("##### Request : " + req.key + ":" + req.val);
            WriteRequest writeRequest = new WriteRequest(req.key, null, req.val);
            loadbalancer.tell(writeRequest, getSelf());
        } else {
            unhandled(msg);
        }
    }

}
