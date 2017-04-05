package processing;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * This class consists of the methods responsible for processing retrieval 
 * requests. These module requests require some sort of logic to process the
 * user request hence for modularity are placed here.
 * @author SENG 401: Group 1
 */
public class ModuleRequestProcessors {
	
	/**
	 * Handle the image retrieval from the google maps servers.
	 * Accepts 2 types of requests map and gmap:
	 * 
	 * map:
	 * 		First gets the longitude and latitude from the aurora locations
	 * 		API module.
	 * 
	 * gmap
	 * 		Directly send the id of the location to the google map server.
	 * @param uriInfo
	 * @return The required image in PNG format
	 * @throws UnirestException
	 */
	public static HttpResponse<InputStream> googleMapsProcessor(String userQuery) throws UnirestException {
		
		String id = getQueryMapid(userQuery);
		
		//The query which will be built to sent to the google maps server
		String googleMapsQuery = "";
		
		// if the request type is "map"
		if(userQuery.contains("type=map")) {

			// Get the locations data from the auroras locations api module
			HttpResponse<JsonNode> response = Unirest
					.get("http://api.auroras.live/v1/?type=locations")
					.asJson();

			JSONObject Jobj = new JSONObject(response.getBody());
			JSONArray Jarr = Jobj.getJSONArray("array");
			String lat = "", lon = "";
			
			// Iterate the recieved jsonArray for the location with the required id 
			for (int i = 0; i < Jarr.length(); i++) {
				JSONObject tmp = Jarr.getJSONObject(i);
				if (tmp.getString("id").equals(id)) {
					// Retrieve the latitude and longitude
					lat = tmp.getString("lat");
					lon = tmp.getString("long");
					break;
				}
			}
			if(lat.equals("")){
				return null;
//				return Response.status(400)
//						.entity("Requested Location id is not listed in the API Locations Module Response")
//						.type("text/plain")
//						.build();
			}
			// Generate the google map query
			googleMapsQuery = "https://maps.googleapis.com/maps/api/staticmap?center="+ lat + "," + lon + 
					"&markers=color:blue%7Clabel:S%7C"+lat+","+lon+
					"&zoom=5&size=400x400&key=AIzaSyDsmWqCoxuSPujk9bkV33DPzuF47HgnAeA";
		}
		else {
			//If the request type is "gmap", directly generate the google maps query using the id
			//entered by the user
			googleMapsQuery = "https://maps.googleapis.com/maps/api/staticmap?center="+ id 
					+"&zoom=5&size=400x400&key=AIzaSyDsmWqCoxuSPujk9bkV33DPzuF47HgnAeA";
		}
		
		// Recieve gm response for the generated query
		HttpResponse<InputStream> gm_response = 
				   Unirest.get(googleMapsQuery)    
				   .asBinary();
		
		// Send the obtained image
//		return Response.status(gm_response.getStatus())
//				.entity(gm_response.getBody())
//				.type("image/jpeg")
//				.build();
		return gm_response;
	}
	
	private static String getQueryMapid(String query) {  
	    String[] params = query.split("&");  
	    Map<String, String> map = new HashMap<String, String>();  
	    for (String param : params) {  
	        String name = param.split("=")[0];  
	        String value = param.split("=")[1];  
	        map.put(name, value);  
	    }  
	    return map.get("id");  
	}
	
	/**
	 * Aurora's response for the location module request consists of a set of
	 * json objects which require a different method a attribution association.
	 * Code is also presented to add attribution as an extra json object in the
	 * set (Disabled for now).
	 * @param userQuery
	 * @return Response
	 * @throws UnirestException
	 */
	public static Response locationRequestProcessor (String userQuery) throws UnirestException{
		
		// Request the required json from the API
		HttpResponse<JsonNode> response = 
		   Unirest.get("http://api.auroras.live/v1/?" + userQuery)             
		   .asJson();
		
		try {
			/*
			 * The Following commented out code adds the attribution to the JSONArray to be recieved by the locations 
			 * module	
			JSONArray JArray = new JSONArray(response.getBody().toString());
			JSONObject Attribution = new JSONObject();
			Attribution.put("attribution","Powered by Auroras.live!");
			JArray.put(Attribution);
			*/
			
			JSONObject json = new JSONObject(response.getBody());
			
			// The Aurora's API returns a JSONArray, so we append the
			// attribution to the array instead of appending it to each location
			json.put("attribution", "Powered by Auroras.live!");
	
			return Response.status(response.getStatus())
				.entity(json.toString())
				.type("application/json")
				.build();
		} catch(JSONException e) {
			// Invalid Response or Request
			return Response.status(400)
				.entity("Aurora Server did not understand the request, please check your request")
				.type("text/plain")
				.build();
		}
	}
}
