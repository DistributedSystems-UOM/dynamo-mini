package akka.dynamo_mini.persistence_engine;

import java.util.Iterator;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * In this implementation of persistence, data store in the memory.
 *
 * @author: Gihan Karunarathne
 * Date: 1/12/14
 * Time: 12:48 AM
 * @email: gckarunarathne@gmail.com
 */
public class Memory<T> implements Persistence<T> {
    private TreeMap<Integer, T> values = new TreeMap<>();

    @Override
    public boolean put(T key, T value) {
        int k = hashFunction.hash((String) key);
        values.put(k, value);
        if (values.get(k) == value)
            return true;
        else return false;
    }

    @Override
    public T get(T key) {
        return values.get(hashFunction.hash(key));
    }

    public T moveData(T startKey, T endKey) {
        NavigableMap navigableMap = values.subMap(hashFunction.hash(startKey), true, hashFunction.hash(endKey), false);
        Set s = navigableMap.entrySet();
        Iterator i = s.iterator();

        while (i.hasNext()) {
            values.remove(i.next());
        }
        return (T) navigableMap;
    }

    public T copyData(T startKey, T endKey) {
        return (T) values.subMap(hashFunction.hash(startKey), true, hashFunction.hash(endKey), false);
    }

    public boolean deleteData(T startKey, T endKey) {
        NavigableMap navigableMap = values.subMap(hashFunction.hash(startKey), true, hashFunction.hash(endKey), false);
        Set s = navigableMap.entrySet();
        Iterator i = s.iterator();

        while (i.hasNext()) values.remove(i.next());

        if (values.subMap(hashFunction.hash(startKey), true, hashFunction.hash(endKey), false).size() == 0)
            return true;
        else
            return false;
    }
}
