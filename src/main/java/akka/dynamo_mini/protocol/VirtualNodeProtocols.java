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

    public static class StateMachinePutRequest implements Serializable {
        private final String key;
        private final Context context;
        private final Object object;

        public StateMachinePutRequest(String key, Context context, Object object){
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

    public static final String BACKEND_REGISTRATION = "BackendRegistration";
}
