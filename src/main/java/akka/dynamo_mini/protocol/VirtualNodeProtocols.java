package akka.dynamo_mini.protocol;

import java.io.Serializable;
import java.util.List;
import java.util.SortedMap;

/**
 * These protocols are used to interact with virtual nodes with each other.
 * As a examples, replication handling, handling when termination of a virtual node and
 * handling when a new virtual node added etc.
 *
 * @author: Gihan Karunarathne
 * Date: 1/14/14
 * Time: 10:46 PM
 * @email: gckarunarathne@gmail.com
 */
public interface VirtualNodeProtocols {

    public static class GetKeyValue implements Serializable {
        private final String key;

        public GetKeyValue(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }

    public static class PutKeyValue implements Serializable {
        private final String key;
        private final Context context;
        private final Object object;

        public PutKeyValue(String key, Context context, Object object) {
            this.key = key;
            this.context = context;
            this.object = object;
        }

        public String getKey() {
            return this.key;
        }

        public Context getContext() {
            return this.context;
        }

        public Object getObjectValue() {
            return this.object;
        }
    }

    public static class ResultsValue implements Serializable {
        private final Object newObject;
        private final Context context;

        public ResultsValue(Object newObject, Context context) {
            this.newObject = newObject;
            this.context = context;
        }

        public Object getNewObject() {
            return this.newObject;
        }

        public Context getContext() {
            return this.context;
        }
    }

    public static class AckToWrite implements Serializable {
        private final boolean newObject;
        private final Context context;

        public AckToWrite(boolean newObject, Context context) {
            this.newObject = newObject;
            this.context = context;
        }

        public boolean isSuccess() {
            return this.newObject;
        }

        public Context getContext() {
            return this.context;
        }
    }

    public static class MoveDataToNewNode implements Serializable {
        private final SortedMap data;

        public MoveDataToNewNode(SortedMap data) {
            this.data = data;
        }

        public SortedMap getData() {
            return this.data;
        }
    }

    public static final String BACKEND_REGISTRATION = "BackendRegistration";
}
