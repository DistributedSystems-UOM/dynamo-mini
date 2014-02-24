package dynamo_server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
import akka.dynamo_mini.clienthandle.DynamoClient;

public class Initialize extends HttpServlet {

	private BufferedReader bufferedReader;
	private ActorSystem system;
	Address contactAdress = Cluster.get(system).selfAddress();
	
	private static String systemName = "Dynamo-client";
    private static FiniteDuration workTimeout = Duration.create(10, "seconds");
    
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		 InputStream stream = this.getClass().getClassLoader().getResourceAsStream("keyVal.txt");

		 bufferedReader = new BufferedReader(new InputStreamReader(stream));
		 String temp = null;
		 String output = "";
		 while ((temp = bufferedReader.readLine()) != null) {
		   String[] keyAndVal = temp.split(":");
		   String key = keyAndVal[0];
		   String val = keyAndVal[1];
		   
		  
		   System.out.println("**** "+"key :" + key +" "+"val :" + val );
		   
		   // call actor
//		   system = ActorSystem.create(systemName);
//		    Set<ActorSelection> initialContacts = new HashSet<ActorSelection>();
//		    initialContacts.add(system.actorSelection(contactAdress + "/user/receptionist"));
//		    ActorRef clusterClient = system.actorOf(ClusterClient.defaultProps(initialContacts),
//		      "clusterClient");
//		    system.actorOf(Props.create(DynamoClient.class), "client1");
		 }
		 
		 	
	        resp.sendRedirect("Client.jsp");


		 
	}
}
