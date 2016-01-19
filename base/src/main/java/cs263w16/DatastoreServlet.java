package cs263w16;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
//import com.google.appengine.repackaged.com.google.datastore.v1.Filter;

@SuppressWarnings("serial")
public class DatastoreServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      resp.setContentType("text/html");
      resp.getWriter().println("<html><body>");
       //Add your code here
      //make sure only two parameter input  or otherwise is wrong
    	  Enumeration<String> em = req.getParameterNames();
    	  while(em.hasMoreElements()){
    		  String s = em.nextElement();
    		  if(!s.equals("keyname")&& !s.equals("value"))
    		  {
    			  resp.getWriter().println("Input is wrong");
    			  return;
    		  }
    		
    	  }
    	
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      String keyname = req.getParameter("keyname");
      String value = req.getParameter("value");
      if (keyname != null) {
      	if (value != null) {
      		//create and store an entity kind taskData in the . print "store key and value in datastore"
      		Entity 	tne = new Entity("TaskData",keyname);
      		tne.setProperty("value", value);
      		Date date = new Date();
      		tne.setProperty("date", date);
      		datastore.put(tne);
      		resp.getWriter().println("<h3>Stored KEY: "+ keyname+ " and VALUE: "+ value+ " in Datastore</h3>");
      	}
      	else{
      		//Display the element of kind TaskData in the
      		    Key k = KeyFactory.createKey("TaskData", keyname);
      		    Filter keyFilter =new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,FilterOperator.EQUAL,k);
      			Query findValue = new Query().setFilter(keyFilter);
      			List<Entity> result = datastore.prepare(findValue).asList(FetchOptions.Builder.withDefaults());
      			resp.getWriter().println("<h3>Display after filtering</h3> ");
      			for(int i =0; i < result.size(); i++){
        		  Entity e = result.get(i);
        		  resp.getWriter().println("<h3>Stored KEY: "+ keyname +  " and VALUE: "+ e.getProperty("value")+ " in Datastore</h3>");
      			}
//					Entity tne = datastore.get(k);
//      		    value = (String) tne.getProperty("value");
//      			Date date  = (Date) tne.getProperty("date");
//      			resp.getWriter().println("<h3>Hello World"+ date +":"+value+ "</h3>");
//      	
      	}
      }
      else{
    	  Query findAll = new Query("TaskData");
    	  List<Entity> result = datastore.prepare(findAll).asList(FetchOptions.Builder.withDefaults());
    	  resp.getWriter().println("<h3>Display existing</h3> ");
    	  for(int i =0; i < result.size(); i++){
    		  Entity e = result.get(i);
    		  resp.getWriter().println("<h3> Created Date: "+ e.getProperty("date") +  " and VALUE: "+ e.getProperty("value")+ " in Datastore</h3>");
  			}
      }

      resp.getWriter().println("</body></html>");
  }
}
