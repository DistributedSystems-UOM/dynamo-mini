package akka.dynamo_mini.protocol;

import javax.naming.Context;
import java.io.Serializable;

/**
 * The protocols to interact with clients. Specially, the two methods of 1. get() and 2. put()
 *
 * @author: Gihan Karunarathne
 * Date: 1/27/14
 * Time: 12:26 AM
 * @email: gckarunarathne@gmail.com
 */
public interface ClientProtocols {

    public static class ReadRequest implements Serializable {
        private final String key;

        public ReadRequest(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }

    public static class WriteRequest implements Serializable {
        private final String key;
        private final Context context;
        private final Object object;

        public WriteRequest(String key, Context context, Object object){
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

    public static class ReadResults implements Serializable {
        private final Object results;

        public ReadResults(Object results) {
            this.results = results;
        }

        public Object getResults() {
            return this.results;
        }
    }

    public static class WriteResults implements Serializable {
        private final Object results;
        private final boolean isSuccess;

        public WriteResults(Object results, boolean success) {
            this.results = results;
            this.isSuccess = success;
        }

        public WriteResults(boolean success){
            this.results = null;
            this.isSuccess = success;
        }

        public Object getResults() {
            return this.results;
        }

        public boolean isSuccess(){
            return  this.isSuccess;
        }
    }
}

