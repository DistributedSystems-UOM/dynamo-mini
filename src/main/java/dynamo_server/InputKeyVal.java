package dynamo_server;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.contrib.pattern.ClusterClient;
import akka.dynamo_mini.Dynamo;
import akka.dynamo_mini.clienthandle.DynamoClient;
import akka.dynamo_mini.protocol.ClientProtocols.WriteRequest;

public class InputKeyVal extends HttpServlet {

    
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String key = req.getParameter("key");
        String value = req.getParameter("value");

        // call actor

        System.out.println("key :" + key + " " + "val :" + value);
        System.out.println("## Done");
        
        Dynamo dynamoIntance = Dynamo.INSTANCE;
        dynamoIntance.createClient(new WriteRequest(key, null, value));
        resp.sendRedirect("Client.jsp");
    }
}
