package acquisition;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.*;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException; 

@Path("/") 
public class RequestHandler {

	@Path("")
	@GET
	@Produces({"image/png","application/json","text/plain"})
	public Response HandleRequest2 (@Context UriInfo uriInfo) throws UnirestException {


		
		String userQuery = uriInfo.getRequestUri().getQuery();
		
		if (userQuery.contains("type=embed") || userQuery.contains("type=images&image")) {
			HttpResponse<InputStream> response = 
					   Unirest.get("http://api.auroras.live/v1/?" + userQuery)       
					   .header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0")       
					   .asBinary();
		
			return Response.status(200)
					.entity(response.getBody())
					.type("image/png")
					.build();
		}
		
		else if(userQuery.contains("type=map")){
			
			MultivaluedMap<String, String> Params = uriInfo.getQueryParameters();
			String id = Params.getFirst("id");
			
			HttpResponse<JsonNode> response = Unirest
					.get("http://api.auroras.live/v1/?type=locations")       
					.header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0")
					.asJson();

			JSONObject abc = new JSONObject(response.getBody());
			JSONArray arr = abc.getJSONArray("array");
			String lat = "", lon = "";
			
			for (int i = 0; i < arr.length(); i++) {
				JSONObject tmp = arr.getJSONObject(i);
				if (tmp.getString("id").equals(id)) {
					lat = tmp.getString("lat");
					lon = tmp.getString("long");
					break;
				}
			}
			
			String googleMapsQuery = "https://maps.googleapis.com/maps/api/staticmap?center="+ lat + "," + lon + 
					"&markers=color:blue%7Clabel:S%7C"+lat+","+lon+"&zoom=5&size=400x400&key=AIzaSyDsmWqCoxuSPujk9bkV33DPzuF47HgnAeA";

			HttpResponse<InputStream> gm_response = 
					   Unirest.get(googleMapsQuery)    
					   .asBinary();
			
			return Response.status(200)
					.entity(gm_response.getBody())
					.type("image/png")
					.build();
		}
		else if (userQuery.contains("type=gmap")){
			MultivaluedMap<String, String> Params = uriInfo.getQueryParameters();
			String id = Params.getFirst("id");
			String googleMapsQuery = "https://maps.googleapis.com/maps/api/staticmap?center="+ id +"&zoom=5&size=400x400&key=AIzaSyDsmWqCoxuSPujk9bkV33DPzuF47HgnAeA";

			HttpResponse<InputStream> gm_response = 
					   Unirest.get(googleMapsQuery)    
					   .asBinary();
			
			return Response.status(200)
					.entity(gm_response.getBody())
					.type("image/png")
					.build();
		}
		else if (userQuery.contains("type=locations")) {
			
			HttpResponse<JsonNode> response = 
					   Unirest.get("http://api.auroras.live/v1/?" + userQuery)             
					   .asJson();
			try{
//			JSONArray JArray = new JSONArray(response.getBody().toString());
//			JSONObject Attribution = new JSONObject();
//			Attribution.put("attribution","Powered by Auroras.live!");
//			JArray.put(Attribution);
			
			JSONObject json = new JSONObject(response.getBody());
			json.put("attribution", "Powered by Auroras.live!");

			return Response.status(200)
					.entity(json.toString())
					.type("application/json")
					.build();
			}catch(JSONException e){
				// Invalid Response or Request
				return Response.status(400)
						.entity("Aurora Server did not understand the request, please check your request")
						.type("text/plain")
						.build();
			}
			
			
		}
		else{
			HttpResponse<JsonNode> response = 
					   Unirest.get("http://api.auroras.live/v1/?" + userQuery)             
					   .asJson();
			try{
			JSONObject json = new JSONObject();
			json = response.getBody().getObject();
			json.put("attribution", "Powered by Auroras.live!");
			return Response.status(200)
					.entity(response.getBody().toString())
					.type("application/json")
					.build();
			}catch(JSONException e){
				// Invalid Response or Request
				return Response.status(400)
						.entity("Aurora Server did not understand the request, please check your request")
						.type("text/plain")
						.build();
			}
		}
	}
	
}
