import javax.ws.rs.GET; 
import javax.ws.rs.Path; 
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException; 
import org.json.JSONObject;
import com.mashape.unirest.http.HttpResponse; 
import com.mashape.unirest.http.JsonNode; 
import com.mashape.unirest.http.Unirest; 
import com.mashape.unirest.http.exceptions.UnirestException; 

@Path("/") 
public class CentralRequestHandler {  

	@Path("")
	@GET
	@Produces("application/json")
	public Response HandleRequest (@Context UriInfo uriInfo)
	throws JSONException, UnirestException {

		String userQuery = uriInfo.getRequestUri().getQuery();
		//JSONObject json = null;// = new JSONObject();
		HttpResponse<JsonNode> response = 
				   Unirest.get("http://api.auroras.live/v1/?"+userQuery)       
				   .header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0")       
				   .asJson();   
			JSONObject json = new JSONObject(response.getBody());
		   json.put("Attribution", "Powered by Auroras.live");
		   //return Response.status(200).entity(response.getBody().toString()).build(); 
		   return Response.status(200).entity(json.toString()).build();
	}
	
}  

/*
@Path("hello")    
@GET    
@Produces("application/json")    
public Response helloWorld() throws JSONException {  
	JSONObject jsonObject = new JSONObject();   
	jsonObject.put("Message", "Hello World!");   
	String result = jsonObject.toString();      
	return Response.status(200).entity(result).build();   
}    
   @Path("user={user}")    
   @GET    
   @Produces("application/json")    
   public Response helloUser(@PathParam("user") String userName) throws JSONException {  
	   JSONObject jsonObject = new JSONObject();   
	   jsonObject.put("Message", "Hello " + userName + "!");   
	   String result = jsonObject.toString();      
	   return Response.status(200).entity(result).build();    
	   }
   @Path("getACE")    
   @GET    
   @Produces("application/json")    
   public Response getACE() throws JSONException, UnirestException {     
	   JSONObject jsonObject = new JSONObject();      
	   HttpResponse<JsonNode> response = 
			   Unirest.get("http://api.auroras.live/v1/?type=ace&data=kp")       
			   .header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0")       
			   .asJson();   
	   jsonObject = response.getBody().getObject();  
	   String att = "Powered by Auroras.live";   
	   jsonObject.put("Attribution", att);   
	   return Response.status(200).entity(response.getBody().toString()).build();    
	   } 
   @Path("getLocation")    
   @GET    
   @Produces("application/json")    
   public Response getLocation() throws JSONException, UnirestException {     
	   JSONObject jsonObject = new JSONObject();      
	   HttpResponse<JsonNode> response = 
			   Unirest.get("http://api.auroras.live/v1/?type=locations")       
			   .header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0")       
			   .asJson();   
	   jsonObject = response.getBody().getObject();  
	   return Response.status(200).entity(response.getBody().toString() + "Powered by Auroras.Live").build();    

MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters(); 
return Response.status(200).entity(queryParams.toString() + "Powered by Auroras.Live").build(); 	   } 
*/

