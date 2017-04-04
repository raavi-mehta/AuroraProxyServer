package acquisition;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.mashape.unirest.http.exceptions.UnirestException;

import processing.CacheController; 

/**
 * This class handles all the requests a user makes. 
 * It work by fetching the entered URI and making the appropriate 
 * request to the aurora server.
 * These requests are handled by the Data Retriever
 * @author SENG 401: Group 1
 */
@Path("/") 
public class RequestHandler {
	
	
	
	/**
	 * This method is called when the user client makes a request.
	 * @param uriInfo
	 * @return
	 * @throws UnirestException
	 */
	@Path("")
	@GET
	@Produces({"image/jpeg", "application/json", "text/plain"}) // The 3 types of responses this server can produce
	public Response HandleRequest (@Context UriInfo uriInfo) throws UnirestException {
		if (!uriInfo.getRequestUri().toString().contains("?type")) {
			return Response.status(400)
					.entity("Welcome to the SENG 401 - Group 1 - Proxy Server"
							+ "for Auroras.live\nTip: We have not recieved a"
							+ "request, please add a request query!\n")
					.type("text/plain")
					.build();
		}
		else {
			try {
				// Call the DataRetriever to get the appropriate information from the main aurora server
				// and return the information to the client (attribution is automatically handled by FetchAurora)
//				String userShit = uriInfo.getRequestUri().getQuery();
//				System.out.println(userShit);
//				try {
//					Response x = (Response) cache.get(userShit).getObjectValue();
//					System.out.println("In Cache");
//					return x;
//				} catch (NullPointerException e) {
//					Response x = DataRetriever.FetchAurora(uriInfo);
//					cache.put(new Element(userShit, x));
//					System.out.println("Put in cache");
//					return x;
//				}
				//return DataRetriever.FetchAurora(uriInfo);
				return CacheController.cacheProcess(uriInfo);
			} catch (Exception e) {
				return Response.status(400)
						.entity("Aurora Server did not understand the request, please check your request")
						.type("text/plain")
						.build();
			}
		}
	}

}
