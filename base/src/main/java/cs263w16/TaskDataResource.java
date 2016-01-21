package cs263w16;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.logging.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import javax.xml.bind.JAXBElement;

public class TaskDataResource {
  @Context
  UriInfo uriInfo;
  @Context
  Request request;
  String keyname;

  public TaskDataResource(UriInfo uriInfo, Request request, String kname) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.keyname = kname;
  }
  // for the browser
  @GET
  @Produces(MediaType.TEXT_XML)
  public TaskData getTaskDataHTML() {
	  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	  MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	  Key k = KeyFactory.createKey("TaskData", this.keyname);
	  if(syncCache.contains(this.keyname)){
		  Entity tne = (Entity) syncCache.get(this.keyname);
		  Date date = new Date();
		  String value = (String) tne.getProperty("value");
		  TaskData td = new TaskData(this.keyname, value,date);
		  return td;
	  }
	  else try{
		  Entity tne = datastore.get(k);
		  Date date = new Date();
		  String value = (String) tne.getProperty("value");
		  TaskData td = new TaskData(this.keyname, value, date);
		  return td;
		  
	  }catch(EntityNotFoundException e){
		  throw new RuntimeException("Get: Taskdata with" + this.keyname+ "not found");
	  }
    //add your code here (get Entity from datastore using this.keyname)
    // throw new RuntimeException("Get: TaskData with " + keyname +  " not found");
    //if not found
  }
  // for the application
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public TaskData getTaskData() {
    //same code as above method
	  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	  MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	  Key k = KeyFactory.createKey("TaskData", this.keyname);
	  if(syncCache.contains(this.keyname)){
		  Entity tne = (Entity) syncCache.get(this.keyname);
		  Date date = new Date();
		  String value = (String) tne.getProperty("value");
		  TaskData td = new TaskData(this.keyname, value,date);
		  return td;
	  }
	  else try{
		  Entity tne = datastore.get(k);
		  Date date = new Date();
		  String value = (String) tne.getProperty("value");
		  TaskData td = new TaskData(this.keyname, value, date);
		  return td;
		  
	  }catch(EntityNotFoundException e){
		  throw new RuntimeException("Get: Taskdata with" + this.keyname+ "not found");
	  }
	  
  }

  @PUT
  @Consumes(MediaType.APPLICATION_XML)
  public Response putTaskData(String val) {
    Response res = null;
    //add your code here
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
    
    Key k = KeyFactory.createKey("TaskData", this.keyname);
    
    //first check if the Entity exists in the datastore
    try{
    	Entity tne = datastore.get(k);
    	//else signal that we updated the entity
    	tne.setProperty("value", val);
    	tne.setProperty("date", new Date());
    	datastore.put(tne);
    	syncCache.put(this.keyname, tne);
        res = Response.noContent().build();
    }catch(EntityNotFoundException e){
    	//if it is not, create it and 
        //signal that we created the entity in the datastore 
    	Entity tne = new Entity("TaskData", this.keyname);
    	tne.setProperty("value", val);
    	tne.setProperty("date", new Date());
    	datastore.put(tne);
    	syncCache.put(this.keyname, tne);
        res = Response.created(uriInfo.getAbsolutePath()).build();
    }
     
    return res;
  }

  @DELETE
  public void deleteIt() {
	  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	  MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
	  Key k = KeyFactory.createKey("TaskData", this.keyname);
	  try{
		  Entity tne = datastore.get(k);
		  datastore.delete(k);
		  syncCache.delete(this.keyname);
		  
	  }catch(EntityNotFoundException e){
		  System.out.println("TaskDate with keyname "+ this.keyname + "not found");
	  }
    //delete an entity from the datastore
    //just print a message upon exception (don't throw)
    }
  }
