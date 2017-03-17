package processing;

import javax.ws.rs.core.UriInfo;

import org.json.JSONException; 
import org.json.JSONObject;
import com.mashape.unirest.http.HttpResponse; 
import com.mashape.unirest.http.JsonNode; 
import com.mashape.unirest.http.Unirest; 
import com.mashape.unirest.http.exceptions.UnirestException; 

public class DataRetriever {  

	public static JSONObject FetchAurora (UriInfo uriInfo) throws JSONException, UnirestException {
		JSONObject json = null;
		String userQuery = uriInfo.getRequestUri().getQuery();
		if (userQuery.contains("type=embed")||userQuery.contains("type=images&image")){
			
		}
		else{
		HttpResponse<JsonNode> response = 
				   Unirest.get("http://api.auroras.live/v1/?"+userQuery)       
				   .header("cookie", "PHPSESSID=MW2MMg7reEHx0vQPXaKen0")       
				   .asJson();  
		
		json = new JSONObject(response.getBody());
		
		json.put("Attribution", "Powered by Auroras.live");
		}
		return json;
	}
	
}  

