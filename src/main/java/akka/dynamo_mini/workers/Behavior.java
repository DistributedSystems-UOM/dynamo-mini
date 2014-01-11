package akka.dynamo_mini.workers;

import akka.japi.Procedure;

public abstract class Behavior implements Procedure<Object> {
  public abstract void apply(Object message);
}
