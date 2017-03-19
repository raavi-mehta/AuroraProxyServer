package processing;

import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * This class consists of the methods responsible for processing retrival requests.
 * These module requests require some sort of logic to process the user request hence
 * for modularity are placed here.
 * @author SENG 401: Group 1
 *
 */
public class ModuleRequestProcessors {
	
	/**
	 * Handle the image retrieval from the google maps servers.
	 * Accepts 2 types of requests map and gmap 
	 * map - first gets the longitude and latitude from the aurora locations API module
	 * gmap - directly send the id of the location to the google map server
	 * @param uriInfo
	 * @return The required image in PNG format
	 * @throws UnirestException
	 */
	public static Response GoogleMaps_Processor(UriInfo uriInfo) throws UnirestException{
		
		// Create a map of the user entered query parameters to get id and other required information
		MultivaluedMap<String, String> Params = uriInfo.getQueryParameters();
		
		// id of the location
		String id = Params.getFirst("id");
		
		//The query which will be built to sent to the google maps server
		String googleMapsQuery = "";
		
		// if the request type is "map"
		if(Params.getFirst("type").equals("map")){

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
		return Response.status(200)
				.entity(gm_response.getBody())
				.type("image/png")
				.build();
	}
	
	/**
	 * Aurora's reponse for the location module request consists of a set of json objects which require
	 * a different method a attribution association. 
	 * Code is also presented to add attribution as an extra json onject in the set (Disabled for now).
	 * @param userQuery
	 * @return
	 * @throws UnirestException
	 */
	public static Response LocationRequest_Processor (String userQuery) throws UnirestException{
		
		// Request the required json from the API
		HttpResponse<JsonNode> response = 
				   Unirest.get("http://api.auroras.live/v1/?" + userQuery)             
				   .asJson();
		try{
			
		
		/*
		 * The Following commented out code adds the attribution to the JSONArray to be recieved by the locations 
		 * module	
		JSONArray JArray = new JSONArray(response.getBody().toString());
		JSONObject Attribution = new JSONObject();
		Attribution.put("attribution","Powered by Auroras.live!");
		JArray.put(Attribution);
		*/
		JSONObject json = new JSONObject(response.getBody());
		// The Auroras API returns a JSONArray, so we append the attribution to the array instead of appending it to
		// each location
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
}
