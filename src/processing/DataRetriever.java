package processing;

import java.io.InputStream;

import javax.ws.rs.core.Response;

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
 */
public class DataRetriever {  


	/**
	 * A fork method that directs the incoming request to the appropriate
	 * processing module or retrieval method.
	 * @param uriInfo
	 * @return Appropriate HTTP response to be sent to the client
	 * @throws JSONException
	 * @throws UnirestException
	 */
	public static Response FetchAurora (String userQuery) throws JSONException,
		UnirestException {
		
		HttpResponse<InputStream> imgR = null;
		
		// If the Module referred is such that it is supposed to return an image
		if (userQuery.contains("type=embed") || userQuery.contains("type=images&image")) {
			imgR = AuroraImageRetrieval(userQuery);
			return Response.status(imgR.getStatus())
					.entity(imgR.getBody())
					.type("image/jpeg")
					.build();
		}
		
		else if(userQuery.contains("type=map") || userQuery.contains("type=gmap")) {
			imgR = ModuleRequestProcessors.googleMapsProcessor(userQuery);
			return Response.status(imgR.getStatus())
					.entity(imgR.getBody())
					.type("image/jpeg")
					.build();
		}
		
		// If the request is needed to be processed by the locations module
		else if (userQuery.contains("type=locations")) {
			return ModuleRequestProcessors.locationRequestProcessor(userQuery);
		}
		
		/* All other module requests are served here
		 * In case of improper requests, such are sent to the Aurora server and
		 * if an invalid response is received an error message is produced.
		*/
		else { //General Request Processing
			return GeneralRequest(userQuery);
		}
	}
	/**
	 * A fork method that directs the incoming request to the appropriate
	 * processing module or retrieval method.
	 * @param uriInfo
	 * @return Appropriate HTTP response to be sent to the client
	 * @throws JSONException
	 * @throws UnirestException
	 */
	public static HttpResponse<InputStream> FetchAuroraImages (String userQuery) throws JSONException,
		UnirestException {
		// If the Module referred is such that it is supposed to return an image
		if (userQuery.contains("type=embed") || userQuery.contains("type=images&image")) {
			return AuroraImageRetrieval(userQuery);
		}
		
		else if(userQuery.contains("type=map") || userQuery.contains("type=gmap")) {
			return ModuleRequestProcessors.googleMapsProcessor(userQuery);
		}
		return null;
		
	
	}
	
	public static Response FetchAuroraLocations (String userQuery) throws JSONException,
		UnirestException {
		return ModuleRequestProcessors.locationRequestProcessor(userQuery);
	}
	
	public static Response FetchAuroraGeneral (String userQuery) throws JSONException,
		UnirestException {
		return GeneralRequest(userQuery);
	}
	/**
	 * This method is used for retrieving images from the aurora API.
	 * @param userQuery
	 * @return an http response with the retrieved image as a PNG
	 */
	public static HttpResponse<InputStream> AuroraImageRetrieval(String userQuery) {

		// Receive Image from the Aurora Server
			HttpResponse<InputStream> response = null;
			try {
				response = Unirest.get("http://api.auroras.live/v1/?" + userQuery)       
				   .header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0")       
				   .asBinary();
			} catch (UnirestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return response;
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
		try {
			//Add attribution to the recieved json object
			JSONObject json = new JSONObject();
			json = response.getBody().getObject();
			json.put("attribution", "Powered by Auroras.live!");
		
			// Return appropriate response
			return Response.status(response.getStatus())
				.entity(response.getBody().toString())
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

