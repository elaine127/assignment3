package cs263w16;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//The Enqueue servlet should be mapped to the "/enqueue" URL.
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class TaskQueue extends HttpServlet {
 protected void doPost(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
     String key = request.getParameter("keyname");
     String value = request.getParameter("value");
     // Add the task to the default queue.
     Queue queue = QueueFactory.getDefaultQueue();
     queue.add(TaskOptions.Builder.withUrl("/rest/ds").param("keyname", key).param("value", value));

     response.sendRedirect("/done.html");
 }
}
