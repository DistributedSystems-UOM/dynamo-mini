package akka.dynamo_mini.protocol;

import java.io.Serializable;

public class PutReq implements Serializable {

    public final String key;
    public final String value;

    public PutReq(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
