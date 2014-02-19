package akka.dynamo_mini.persistence_engine;

import akka.dynamo_mini.node_management.HashFunction;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/12/14
 * Time: 12:47 AM
 * @email: gckarunarathne@gmail.com
 */
public interface Persistence<T> {
    final HashFunction hashFunction = new HashFunction();
    /**
     * Store/put a value to the virtual node storage.
     *
     * @param key   key value
     * @param value value to be store
     * @return send object version which is stored
     */
    public T put(T key, T value);

    /**
     * Get a value from the virtual node's storage.
     *
     * @param key key
     * @return object/value which maps to given key
     */
    public T get(T key);

    /**
     * @deprecated Need to get an ack to remove data.
     * Get send  values range which is move into another virtual node
     *
     * @param startKey starting key value of the range which want to move
     * @param endKey   end key value of the range which want to move
     * @return value set for given key range
     * @warning: remove values within the key range
     */
    public T moveData(T startKey, T endKey);

    /**
     * Get send values range which is move into another virtual node
     *
     * @param startKey starting key value of the range which want to move
     * @param endKey   end key value of the range which want to move
     * @return value set for given key range
     */
    public T copyData(T startKey, T endKey);

    /**
     * Delete values in a given range which was moved into another virtual node
     *
     * @param startKey starting key value of the range which want to move
     * @param endKey   end key value of the range which want to move
     * @return true if successfully deleted values, false otherwise
     */
    public boolean deleteData(T startKey, T endKey);
}
