package akka.dynamo_mini;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/4/14
 * Time: 6:04 PM
 * @email: gckarunarathne@gmail.com
 */

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.dynamo_mini.persistence_engine.MySQL;
import akka.dynamo_mini.persistence_engine.Persistence;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.binary.Hex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static akka.dynamo_mini.protocol.VirtualNodeProtocols.*;

public class VirtualNode extends UntypedActor {

    DigestUtils d = new DigestUtils();
    Cluster cluster = Cluster.get(getContext().system());
    List<ActorRef> backends = new ArrayList<ActorRef>();
    int jobCounter = 0;

    //subscribe to cluster changes, MemberUp
    @Override
    public void preStart() {
        cluster.subscribe(getSelf(), MemberUp.class);
    }

    //re-subscribe when restart
    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public void onReceive(Object message) {
        if(message instanceof PutKeyValue){
            PutKeyValue putKeyValue = (PutKeyValue) message;

        } else if(message instanceof StateMachinePutRequest){
            StateMachinePutRequest stateMachinePutRequest = (StateMachinePutRequest) message;
            Persistence persistence = new MySQL();
            persistence.get(stateMachinePutRequest.getKey());
        }

        if (message instanceof TransformationJob) {
            TransformationJob job = (TransformationJob) message;
            getSender()
                    .tell(new TransformationResult(job.getText().toUpperCase()),
                            getSelf());

        } else if (message instanceof CurrentClusterState) {
            CurrentClusterState state = (CurrentClusterState) message;
            for (Member member : state.getMembers()) {
                if (member.status().equals(MemberStatus.up())) {
                    register(member);
                }
            }

        } else if (message instanceof MemberUp) {
            MemberUp mUp = (MemberUp) message;
            register(mUp.member());

        } else {
            unhandled(message);
        }

        if ((message instanceof TransformationJob) && backends.isEmpty()) {
            TransformationJob job = (TransformationJob) message;
            getSender().tell(
                    new JobFailed("Service unavailable, try again later", job),
                    getSender());

        } else if (message instanceof TransformationJob) {
            TransformationJob job = (TransformationJob) message;
            jobCounter++;
            backends.get(jobCounter % backends.size())
                    .forward(job, getContext());

        } else if (message.equals(BACKEND_REGISTRATION)) {
            getContext().watch(getSender());
            backends.add(getSender());

        } else if (message instanceof Terminated) {
            Terminated terminated = (Terminated) message;
            backends.remove(terminated.getActor());

        } else {
            unhandled(message);
        }
    }

    void register(Member member) {
        if (member.hasRole("frontend"))
            getContext().actorSelection(member.address() + "/user/frontend").tell(
                    BACKEND_REGISTRATION, getSelf());
    }

    public String getNodeId(String key) {
        return  DigestUtils.sha1Hex(key);
    }

    public static final class KeyValue implements Serializable {
        public final String workId;
        public final Object value;

        public KeyValue(String workId, Object job) {
            this.workId = workId;
            this.value = job;
        }

        @Override
        public String toString() {
            return "Work{" +
                    "workId='" + workId + '\'' +
                    ", job=" + value +
                    '}';
        }
    }
}
