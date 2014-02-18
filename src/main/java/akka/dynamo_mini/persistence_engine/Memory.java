package akka.dynamo_mini.persistence_engine;

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
        T putValue = values.put(k, value);
        return putValue;
    }

    @Override
    public T get(T key) {
        return values.get(key);
    }

    public T moveData(T startKey, T endKey) {
        return null;
    }

    public T copyData(T startKey, T endKey) {
        return null;
    }
}
