package acquisition;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONObject;

import com.mashape.unirest.http.exceptions.UnirestException;

import processing.DataRetriever; 

@Path("/") 
public class RequestHandler {

	@Path("")
	@GET
	@Produces("application/json")
	public Response HandleRequest (@Context UriInfo uriInfo) throws UnirestException {

		String userQuery = uriInfo.getRequestUri().getQuery();
		// TODO Calls to other modules
		JSONObject json = DataRetriever.FetchAurora(userQuery);
		return Response.status(200).entity(json.toString()).build();
	}
	
}
