package akka.dynamo_mini.protocol;

import java.io.Serializable;

/**
 * The protocols to interact with State Machine and preference list nodes.
 * Specially, the two methods of 1. get() and 2. put()
 *
 * This protocol may differ from other read/write request due to handling version of object values in store.
 *
 * @author: Gihan Karunarathne
 * Date: 1/27/14
 * Time: 12:26 AM
 * @email: gckarunarathne@gmail.com
 */
public interface StateMachineProtocols {
    public static class QuorumReadRequest implements Serializable {
        private final String key;

        public QuorumReadRequest(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }

    public static class QuorumWriteRequest implements Serializable {
        private final String key;

        public QuorumWriteRequest(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
}
