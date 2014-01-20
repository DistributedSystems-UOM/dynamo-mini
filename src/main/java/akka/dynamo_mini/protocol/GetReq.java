package akka.dynamo_mini.protocol;

import java.io.Serializable;

public class GetReq implements Serializable {

    public final String key;

    public GetReq(String key) {
        this.key = key;
    }
    
}
