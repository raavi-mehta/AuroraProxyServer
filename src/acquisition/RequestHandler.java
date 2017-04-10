package acquisition;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.mashape.unirest.http.exceptions.UnirestException;

import processing.CacheController;
import processing.DataRetriever; 

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
	public Response HandleRequest(@Context UriInfo uriInfo) throws UnirestException {
		
		String userQuery = uriInfo.getRequestUri().getQuery();
		
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
				if (userQuery.contains("&no-caching=true")) {
					userQuery = userQuery.replace("&no-caching=true", "");
					System.out.println("No-Caching Enabled");
					return DataRetriever.FetchAurora(userQuery);
				}
				
				//return DataRetriever.FetchAurora(uriInfo);
				return CacheController.cacheProcess(userQuery);
			} catch (Exception e) {
				return Response.status(400)
						.entity("Aurora Server did not understand the request, please check your request")
						.type("text/plain")
						.build();
			}
		}
	}
	
	/**
	 * Allows the user to view the administrative stats such as the number of
	 * hits and misses, and the current lifespans of each query/cache type.
	 * 
	 * This page may be access at <context root>/vX/admin/stats
	 * @return
	 */
	@Path("admin/stats")
	@GET
	@Produces("text/plain") 
	public Response StatsView() {
		return Response.status(200)
				.entity("Hits: " + CacheController.getHits() + System.lineSeparator() +
						"Misses: " + CacheController.getMisses() + System.lineSeparator() +
						"Current lifespans (seconds)" + System.lineSeparator() +
						CacheController.getLifespans())
				.type("text/plain")
				.build();
	}
	
	/**
	 * Allows the user to clear all the caches at.
	 * 
	 * This may be access by opening the page <context root>/vX/admin/clear
	 * @return
	 */
	@Path("admin/clear")
	@GET
	@Produces("text/plain") 
	public Response ClearCache() {
		CacheController.clearCache();
		return Response.status(200)
				.entity("Cache Cleared.")
				.type("text/plain")
				.build();
	}
	
	/**
	 * Allows the user to set the caching lifespans for general queries,
	 * location queries, and image queries. These lifespans (in seconds) are
	 * entered as uri parameters general, location, and image respectively.
	 * 
	 * Ex: <context root>/vX/admin/lifespan?general=60&location=1000&image=15
	 * @param uriInfo
	 * @return
	 */
	@Path("admin/lifespan")
	@GET
	@Produces("text/plain") 
	public Response SetCacheLifespans(@Context UriInfo uriInfo) {
		
		MultivaluedMap<String, String> param = uriInfo.getQueryParameters();
		
		if (param.containsKey("general")) {
			int lifespan = Integer.parseInt(param.getFirst("general"));
			CacheController.setGeneralLifespan(lifespan);
		}
		
		if (param.containsKey("location")) {
			int lifespan = Integer.parseInt(param.getFirst("location"));
			CacheController.setLocationLifespan(lifespan);
		}
		
		if (param.containsKey("image")) {
			int lifespan = Integer.parseInt(param.getFirst("image"));
			CacheController.setImageLifespan(lifespan);
		}
		
		return Response.status(200)
				.entity("Current lifespans" + System.lineSeparator() +
						CacheController.getLifespans())
				.type("text/plain")
				.build();
	}
}
