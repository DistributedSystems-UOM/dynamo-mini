package akka.dynamo_mini.workers;

import akka.actor.Address;

import static akka.dynamo_mini.workers.Main.*;

public class MainManyMasters {

  public static void main(String[] args) throws InterruptedException {
    Address joinAddress = startBackend(null, "backend-shard1");
    Thread.sleep(5000);
    startBackend(joinAddress, "backend-shard1");
    startWorker(joinAddress);
    Thread.sleep(5000);
    startFrontend(joinAddress);

    startBackend(joinAddress, "backend-shard2");
    startBackend(joinAddress, "backend-shard2");
    startWorker(joinAddress);
    startWorker(joinAddress);
    startWorker(joinAddress);
  }

}