package dynamo_server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import akka.dynamo_mini.protocol.ClientProtocols.ReadRequest;

import akka.dynamo_mini.Dynamo;
import akka.dynamo_mini.protocol.ClientProtocols.WriteRequest;

public class PullReq extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
       String key = (String) req.getParameter("key");
       System.out.println(key);
       Dynamo dynamoIntance = Dynamo.INSTANCE;
       dynamoIntance.createClient(new ReadRequest(key));
       resp.sendRedirect("Client.jsp");
    }
}
