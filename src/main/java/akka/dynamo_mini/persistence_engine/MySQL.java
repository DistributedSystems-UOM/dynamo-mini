package akka.dynamo_mini.persistence_engine;

import akka.dynamo_mini.VirtualNode;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/12/14
 * Time: 12:48 AM
 * @email: gckarunarathne@gmail.com
 */
public class MySQL implements Persistence{
    @Override
    public VirtualNode.KeyValue put(String key, VirtualNode.KeyValue value) {
        return null;
    }

    @Override
    public VirtualNode.KeyValue get(String key) {
        return null;
    }
}
