package akka.dynamo_mini.persistence_engine;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/12/14
 * Time: 12:47 AM
 * @email: gckarunarathne@gmail.com
 */
public interface Persistence<T> {
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
}
