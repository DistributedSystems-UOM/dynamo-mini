package akka.dynamo_mini.persistence_engine;

import akka.dynamo_mini.protocol.VirtualNodeProtocols.*;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/12/14
 * Time: 12:48 AM
 * @email: gckarunarathne@gmail.com
 */
public class Memory implements Persistence {
    @Override
    public KeyValue put(String key, KeyValue value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public KeyValue get(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
