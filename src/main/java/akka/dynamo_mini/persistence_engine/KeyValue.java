package akka.dynamo_mini.persistence_engine;

import java.io.Serializable;

public final class KeyValue implements Serializable {
    public final String workId;
    public final Object value;

    public KeyValue(String workId, Object job) {
        this.workId = workId;
        this.value = job;
    }

    @Override
    public String toString() {
        return "Work{" +
                "workId='" + workId + '\'' +
                ", job=" + value +
                '}';
    }
}