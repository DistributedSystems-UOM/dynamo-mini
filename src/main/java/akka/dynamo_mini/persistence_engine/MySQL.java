package akka.dynamo_mini.persistence_engine;

import akka.dynamo_mini.VirtualNode;
import akka.dynamo_mini.protocol.VirtualNodeProtocols.*;

/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/12/14
 * Time: 12:48 AM
 * @email: gckarunarathne@gmail.com
 */
public class MySQL<T> implements Persistence<T>{
    @Override
    public boolean put(T key, T value) {
        return false;
    }

    @Override
    public T get(T key) {
        return null;
    }

    @Override
    public T moveData(T startKey, T endKey) {
        return null;
    }

    @Override
    public T copyData(T startKey, T endKey) {
        return null;
    }

    public boolean deleteData(T startKey, T endKey){
        return false;
    }
}
