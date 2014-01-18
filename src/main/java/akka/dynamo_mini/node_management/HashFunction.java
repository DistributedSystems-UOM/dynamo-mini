package akka.dynamo_mini.node_management;

import org.apache.commons.codec.digest.DigestUtils;

public class HashFunction {

    public Integer hash(String key) {
        Integer hashVal;
        String completeHashVal = DigestUtils.sha1Hex(key); // generate hash
        Long partOfHash = Long.parseLong(completeHashVal.substring(0, 14),16); // get the first 15 digit of generated hash
        hashVal = (int) (partOfHash % Integer.MAX_VALUE); //mapping to 0 - 2^31 -1 range
        return hashVal;
    }
    
    public Integer hash(Object key){
        Integer hashVal;
        String completeHashVal = DigestUtils.sha1Hex(key.toString());
        Long partOfHash = Long.parseLong(completeHashVal.substring(0, 14),16); 
        hashVal = (int) (partOfHash % Integer.MAX_VALUE); 
        return hashVal;
    }
}
