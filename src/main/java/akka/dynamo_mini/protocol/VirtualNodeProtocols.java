package akka.dynamo_mini.protocol;

import javax.naming.Context;
import java.io.Serializable;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/14/14
 * Time: 10:46 PM
 * @email: gckarunarathne@gmail.com
 */
public interface VirtualNodeProtocols {

    public static class GetKeyValue implements  Serializable {
        private final String key;

        public GetKeyValue(String key){
            this.key = key;
        }

        public String getKey(){
            return this.key;
        }
    }

    public static class PutKeyValue implements Serializable {
        private final String key;
        private final Context context;
        private final Object object;

        public PutKeyValue(String key, Context context, Object object){
            this.key = key;
            this.context = context;
            this.object = object;
        }

        public String getKey(){
            return this.key;
        }

        public Context getContext(){
            return this.context;
        }

        public Object getObjectValue(){
            return this.object;
        }
    }

    public static class KeyValue implements Serializable{
        private final Object newObject;

        public KeyValue(Object newObject){
            this.newObject = newObject;
        }

        public Object getNewObject(){
            return this.newObject;
        }
    }

    public static class TransformationJob implements Serializable {
        private final String text;

        public TransformationJob(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public static class TransformationResult implements Serializable {
        private final String text;

        public TransformationResult(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return "TransformationResult(" + text + ")";
        }
    }

    public static class JobFailed implements Serializable {
        private final String reason;
        private final TransformationJob job;

        public JobFailed(String reason, TransformationJob job) {
            this.reason = reason;
            this.job = job;
        }

        public String getReason() {
            return reason;
        }

        public TransformationJob getJob() {
            return job;
        }

        @Override
        public String toString() {
            return "JobFailed(" + reason + ")";
        }
    }

    public static final String BACKEND_REGISTRATION = "BackendRegistration";
}
