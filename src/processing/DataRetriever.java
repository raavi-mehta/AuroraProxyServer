package processing;

import java.io.InputStream;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.json.JSONException;
import org.json.JSONObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException; 

/**
 * This Class represents the Data Retriever Module, it is responsible for 
 * fetching the information from the required server (Google Maps or Aurora).
 * Redirects the request to a module processor if required.
 * @author SENG 401: Group 1
 *
 */
public class DataRetriever {  

	/**
	 * A fork method that directs the incoming request to the appropriate processing module or retrieval method
	 * @param uriInfo
	 * @return Appropriate http response to be sent to the client
	 * @throws JSONException
	 * @throws UnirestException
	 */
	public static Response FetchAurora (UriInfo uriInfo) throws JSONException, UnirestException {
		
		// Generate a User Query String from the UriInfo
		String userQuery = uriInfo.getRequestUri().getQuery();
		
		// If the Module refered is such that it is supposed to return an image
		if (userQuery.contains("type=embed") || userQuery.contains("type=images&image")) {
			return AuroraImageRetrieval(userQuery);
		}
		
		else if(userQuery.contains("type=map")||userQuery.contains("type=gmap")){
			return ModuleRequestProcessors.GoogleMaps_Processor(uriInfo);
		}
		
		// If the request is needed to be processed by the locations module
		else if (userQuery.contains("type=locations")) {
		return ModuleRequestProcessors.LocationRequest_Processor(userQuery);
		}
		
		/* All other module requests are served here
		 * In case of improper requests, such are sent to the auroras server and if an invalid response is 
		 * recieved an error message is produced
		*/
		else{ //General Request Processing
			return GeneralRequest(userQuery);
		}
	}
	
	/**
	 * This method is used for retrieving images from the aurora API.
	 * @param userQuery
	 * @return an http response with the retrieved image as a PNG
	 */
	public static Response AuroraImageRetrieval(String userQuery){
		try{
		// Recieve Image from the Auroras Server
					HttpResponse<InputStream> response = 
							   Unirest.get("http://api.auroras.live/v1/?" + userQuery)       
							   .header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0")       
							   .asBinary();
				
					// Return the required image
					return Response.status(200)
							.entity(response.getBody())
							.type("image/png")
							.build();
					}
					catch(Exception e){
						return Response.status(400)
								.entity("Aurora Server did not understand the request, please check your request")
								.type("text/plain")
								.build();
					}
		}
	
	/**
	 * This methods handles all general requests, mainly from modules that are supposed to return
	 * just a single json object. All adds attribution to the recieved objects
	 * @param userQuery
	 * @return
	 * @throws UnirestException
	 */
	public static Response GeneralRequest(String userQuery) throws UnirestException{
		
		// Send request to the Aurora API 
		HttpResponse<JsonNode> response = 
				   Unirest.get("http://api.auroras.live/v1/?" + userQuery)             
				   .asJson();
		try{
			//Add attribution to the recieved json object
		JSONObject json = new JSONObject();
		json = response.getBody().getObject();
		json.put("attribution", "Powered by Auroras.live!");
		
		// Return appropriate response
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

