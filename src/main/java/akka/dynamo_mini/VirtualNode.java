package akka.dynamo_mini;

import akka.actor.UntypedActor;
import akka.dynamo_mini.protocol.GetReq;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/4/14
 * Time: 6:04 PM
 * @email: gckarunarathne@gmail.com
 */


public class VirtualNode extends UntypedActor {

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof GetReq){
            GetReq getReq = (GetReq) message;
            System.out.println(getReq.key);
        }
    }
}
