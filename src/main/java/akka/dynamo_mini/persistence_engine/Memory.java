package akka.dynamo_mini.persistence_engine;

import java.util.*;

/**
 * In this implementation of persistence, data store in the memory.
 *
 * @author: Gihan Karunarathne
 * Date: 1/12/14
 * Time: 12:48 AM
 * @email: gckarunarathne@gmail.com
 */
public class Memory<T> implements Persistence<T> {
    private SortedMap<Integer, T> values = new TreeMap<>();

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
        SortedMap navigableMap = values.subMap(hashFunction.hash(startKey), hashFunction.hash(endKey));
        Set s = navigableMap.entrySet();
        Iterator i = s.iterator();

        while (i.hasNext()) {
            values.remove(i.next());
        }
        return (T) navigableMap;
    }

    public T copyData(T startKey, T endKey) {
        SortedMap data = new TreeMap();

        if (!values.isEmpty()) {
            data = values.subMap(hashFunction.hash(startKey), hashFunction.hash(endKey));
            /*while (!map.isEmpty()) {
                Map.Entry e = map.pollFirstEntry();
                data.put(e.getKey(), e.getValue());
            }*/
        }
        return (T) data;
    }

    public boolean pasteData(T data) {
        values.putAll((SortedMap) data);
        return true;
    }

    public boolean deleteData(T startKey, T endKey) {
        if (values.isEmpty()) return true;

        SortedMap sortedMap = values.subMap(hashFunction.hash(startKey), hashFunction.hash(endKey));
        Set s = sortedMap.entrySet();
        Iterator i = s.iterator();

        while (i.hasNext()) {
            Map.Entry m = (Map.Entry) i.next();
            values.remove(m.getKey());
        }

        if (values.subMap(hashFunction.hash(startKey), hashFunction.hash(endKey)).size() == 0)
            return true;
        else
            return false;
    }
}