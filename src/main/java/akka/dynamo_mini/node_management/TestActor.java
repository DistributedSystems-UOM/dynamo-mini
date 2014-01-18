package akka.dynamo_mini.node_management;

import akka.actor.UntypedActor;

public class TestActor extends UntypedActor{

    @Override
    public void onReceive(Object msg) throws Exception {
        if(msg instanceof String){
            System.out.println("Received Message : " + msg);
        }
        else
            unhandled(msg);
    }

}
