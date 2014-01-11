package akka.dynamo_mini.persistence_engine;

import static akka.dynamo_mini.VirtualNode.KeyValue;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/12/14
 * Time: 12:47 AM
 * @email: gckarunarathne@gmail.com
 */
public interface Persistence {
    public KeyValue put(String key, KeyValue value);

    public KeyValue get(String key);
}
