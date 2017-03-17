package acquisition;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import processing.DataRetriever; 

@Path("/") 
public class RequestHandler {

//	@Path("")
//	@GET
//	@Produces("application/json")
//	public Response HandleRequest (@Context UriInfo uriInfo) throws UnirestException {
//
//		//
//		// TODO Calls to other modules
//		JSONObject json = DataRetriever.FetchAurora(uriInfo);
//		return Response.status(200).entity(json.toString()).build();
//	}

	@Path("")
	@GET
	@Produces({"image/png","application/json"})
	public Response HandleRequest2 (@Context UriInfo uriInfo) throws UnirestException {

		String userQuery = uriInfo.getRequestUri().getQuery();
		
		if (userQuery.contains("type=embed")||userQuery.contains("type=images&image")){
		HttpResponse<InputStream> response = 
				   Unirest.get("http://api.auroras.live/v1/?"+userQuery)       
				   .header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0")       
				   .asBinary(); 
		// TODO Calls to other modules
		//JSONObject json = DataRetriever.FetchAurora(uriInfo);
		return Response.status(200).entity(response.getBody()).type("image/png").build();
		}
		else{
			HttpResponse<JsonNode> response = 
					   Unirest.get("http://api.auroras.live/v1/?"+userQuery)       
					   .header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0")       
					   .asJson();
			JSONObject json = response.getBody().getObject();
			json.put("attribution", "Powered by Auroras.live!");
			return Response.status(200).entity(response.getBody().toString()).type("application/json").build();
		}
	}
	
}
