package acquisition;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import com.mashape.unirest.http.exceptions.UnirestException;
import processing.DataRetriever; 

/**
 * This class handles all the requests a user makes. 
 * It work by fetching the entered uri and making the appropriate 
 * request to the aurora server.
 * These requests are handled by the Data Retriever
 * @author SENG 401: Group 1
 *
 */
@Path("/") 
public class RequestHandler {

	/**
	 * This method is called whend the user client makes a request.
	 * @param uriInfo
	 * @return
	 * @throws UnirestException
	 */
	@Path("")
	@GET
	@Produces({"image/png","application/json","text/plain"}) // The 3 types of responses this server can produce
	public Response HandleRequest (@Context UriInfo uriInfo) throws UnirestException {
		// Call the DataRetriever to get the appropriate imformation from the main aurora server
		// and return the information to the client (attribution is automatically handled by FetchAurora)
		return DataRetriever.FetchAurora(uriInfo);
	}
	
}
