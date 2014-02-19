package akka.dynamo_mini.node_management;

import akka.actor.ActorRef;

import java.util.*;

public class ConsistentHash<T> {

    private final HashFunction hashFunction;
    private final int numberOfReplicas;
    /**
     * How many times same node should assign to the ring.
     * E.x. Setting this value to 2, same virtual node may represent two times in the ring.
     */
    private final int numberOfPositions = 1;
    private final SortedMap<Integer, T> ring = new TreeMap<>();

    public ConsistentHash(HashFunction hashFunction, int numberOfReplicas, Collection<T> nodes) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;

        for (T node : nodes) {
            add(node);
        }
    }

    public void add(T node) {
        ActorRef actorRef = (ActorRef) node;
        ring.put(hashFunction.hash(actorRef.path().name()), node);
        /*for (int i = 0; i < numberOfPositions; i++) {
            ring.put(hashFunction.hash(node.toString() + i), node);
        }*/
    }

    public void remove(T node) {
        ActorRef actorRef = (ActorRef) node;
        ring.remove(hashFunction.hash(actorRef.path().name()));
        /*for (int i = 0; i < numberOfPositions; i++) {
            ring.remove(hashFunction.hash(node.toString() + i));
        }*/
    }

    public T get(Object key) {
        if (ring.isEmpty()) {
            return null;
        }
        int hash = hashFunction.hash(key);

        if (!ring.containsKey(hash)) {
            SortedMap<Integer, T> tailMap = ring.tailMap(hash);
            hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        }
        return ring.get(hash);
    }

    public ArrayList<T> getPreferenceList(Object key) {
        if (ring.isEmpty()) {
            return null;
        }
        int hash = hashFunction.hash(key);
        ArrayList<T> preferenceList = new ArrayList<>();

        Set s = ring.tailMap(hash).entrySet();
        //System.out.println("Preference List size: " + s.size() + " / " + ring.size());

        // Using iterator in SortedMap
        Iterator i = s.iterator();
        int cnt = 0;
        // System.out.println("### replicas " + numberOfReplicas);
        while (i.hasNext() && cnt++ < numberOfReplicas) {
            Map.Entry m = (Map.Entry) i.next();
            preferenceList.add((T) m.getValue());
        }
        if (s.size() < numberOfReplicas){
            s = ring.entrySet();
            i = s.iterator();
            while (i.hasNext() && cnt++ < numberOfReplicas) {
                Map.Entry m = (Map.Entry) i.next();
                preferenceList.add((T) m.getValue());
            }
        }
        // printRing();
        return preferenceList;
    }

    /**
     * Iterate though the ring and print all elements.
     */
    private void printRing() {
        Set s = ring.entrySet();
        Iterator i = s.iterator();
        while (i.hasNext()) {
            Map.Entry m = (Map.Entry) i.next();

            System.out.println("Key:" + m.getKey() + " , value:" + m.getValue());
        }
    }
}
