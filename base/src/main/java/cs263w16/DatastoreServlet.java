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
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
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
      MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
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
      		
//      	syncCache.put(keyname, value);
//    		resp.getWriter().println("<h3>Stored KEY: "+ keyname+ " and VALUE: "+ value+ " in Memcache</h3>");  	     
//      	
      	}
      	else{
      		//Display the element of kind TaskData in the
      		    //check memcache fist
      		    if(!syncCache.contains(keyname))
      		    {
      		    	resp.getWriter().println("Not in MemCache!");
      		    
      		    //check ds
      		    	Key k = KeyFactory.createKey("TaskData", keyname);
      		    	Filter keyFilter =new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,FilterOperator.EQUAL,k);
      		    	Query findValue = new Query().setFilter(keyFilter);
      		    	List<Entity> result = datastore.prepare(findValue).asList(FetchOptions.Builder.withDefaults());
      		    	if(result.size()==0) 
      		    		resp.getWriter().println("Not in DataStore!");
      		    	else{
      		    		resp.getWriter().println("In DataStore!");
      		    		// copy one to the memcache	
      		    		for(int i =0; i < result.size(); i++){
      		    			Entity e = result.get(i);
      		    			syncCache.put(keyname, e.getProperty("value"));
      		    			resp.getWriter().println("<h3>Stored KEY: "+ keyname +  " and VALUE: "+ e.getProperty("value")+ " in Datastore</h3>");
      		    			}      	
      		    		}
      		    }else
      		    	{
      		    	resp.getWriter().println("In Memcache!");
      		    	resp.getWriter().println("<h3>KEY: "+ keyname +  " and VALUE: "+syncCache.get(keyname)+ " value"+ " in Memcache</h3>");
    			
      		    	}
      }
      	}
      else{
    	  //display the entities in datastore and memcache.
    	  Query findAll = new Query("TaskData");
    	  List<Entity> result = datastore.prepare(findAll).asList(FetchOptions.Builder.withDefaults());
    	  for(int i =0; i < result.size(); i++){
    		  Entity e = result.get(i);
    		  resp.getWriter().println("<h3> DataStore Created Date: "+ e.getProperty("date") +  " and VALUE: "+ e.getProperty("value")+"</h3>");
    		  if(syncCache.contains(e.getKey().getName()))
    			  {
    			  resp.getWriter().println("<h3> Memcache Created Date: "+ e.getProperty("date") +  " and VALUE: "+ e.getProperty("value")+ "</h3>");
    			  }
  			}
      }

      resp.getWriter().println("</body></html>");
  }
}
