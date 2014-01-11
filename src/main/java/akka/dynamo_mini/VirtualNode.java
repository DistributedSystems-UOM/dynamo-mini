package akka.dynamo_mini;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/4/14
 * Time: 6:04 PM
 * @email: gckarunarathne@gmail.com
 */
import akka.actor.UntypedActor;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;

public class VirtualNode extends UntypedActor {

    DigestUtils d = new DigestUtils();

    public void onReceive(Object message) throws Exception{

    }

    public String getNodeId(String key){
        return DigestUtils.sha1Hex(key);
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
