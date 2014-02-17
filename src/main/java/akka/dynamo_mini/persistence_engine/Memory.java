package akka.dynamo_mini.persistence_engine;

import akka.dynamo_mini.protocol.VirtualNodeProtocols.KeyValue;

import java.util.Hashtable;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/12/14
 * Time: 12:48 AM
 * @email: gckarunarathne@gmail.com
 */
public class Memory implements Persistence {
    private Hashtable<String, Object> values = new Hashtable<>();

    @Override
    public KeyValue put(String key, KeyValue value) {
        return new KeyValue(values.put(key, value.getNewObject()));
    }

    @Override
    public KeyValue get(String key) {
        return new KeyValue(values.get(key));
    }
}
