package akka.dynamo_mini.persistence_engine;

import static akka.dynamo_mini.protocol.VirtualNodeProtocols.*;
import java.util.*;
/**
 * Class Description.
 *
 * @author: Gihan Karunarathne
 * Date: 1/12/14
 * Time: 12:47 AM
 * @email: gckarunarathne@gmail.com
 */
public interface Persistence<T> {
    public T put(T key, T value);

    public T get(T key);
}
