package akka.dynamo_mini.persistence_engine;

import akka.dynamo_mini.protocol.VirtualNodeProtocols;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.KeyValue;

import java.util.Hashtable;
import java.util.TreeMap;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/12/14
 * Time: 12:48 AM
 * @email: gckarunarathne@gmail.com
 */
public class Memory<T> implements Persistence<T> {
    private TreeMap<String, T> values = new TreeMap<>();

    @Override
    public T put(T key, T value) {
        String k = (String) key;
        T put = values.put(k, value);
        return put;
    }

    @Override
    public T get(T key) {
        //return new KeyValue(values.get(key));
        return null;
    }


}
