package acquisition;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
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
	
	private static CacheManager manager = null;
	private static Cache cache = setupCache();
	
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
				
//				setup cache
//				
//				check if query is in cache
//					return that
//							
//				if not call fetch arorA
//					put thing in cache
//				return response
				String userShit = uriInfo.getRequestUri().getQuery();
				System.out.println(userShit);
				try {
					Response x = (Response) cache.get(userShit).getObjectValue();
					System.out.println("In Cache");
					return x;
				} catch (NullPointerException e) {
					Response x = DataRetriever.FetchAurora(uriInfo);
					cache.put(new Element(userShit, x));
					System.out.println("Put in cache");
					return x;
				}
				

			} catch (Exception e) {
				return Response.status(400)
						.entity("Aurora Server did not understand the request, please check your request")
						.type("text/plain")
						.build();
			}
		}
	}
	
	private static Cache setupCache() {
		//Create a Cache specifying its configuration.
		CacheManager manager = CacheManager.create();
		Cache syscache = new Cache(
		  new CacheConfiguration("syscache", 10000)
		    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
		    .eternal(false)
		    .timeToLiveSeconds(60)
		    .timeToIdleSeconds(30)
		    .diskExpiryThreadIntervalSeconds(0)
		    .persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));
		 
		manager.addCache(syscache);
		return syscache;
	}
	
	public static Cache getCache() {
		return cache;
	}
	
//	public static void main(String[] args) throws InterruptedException {
//		
//		CacheManager manager = CacheManager.create();
//		
//		System.out.println("Hello");
//		Cache syscache = new Cache(
//		  new CacheConfiguration("syscache", 10000)
//		    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
//		    .eternal(false)
//		    .timeToLiveSeconds(4)
//		    .timeToIdleSeconds(60)
//		    .diskExpiryThreadIntervalSeconds(0)
//		    .persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));
//		
//		manager.addCache(syscache);
//		syscache.put(new Element("key1", "stuff"));
//		TimeUnit.SECONDS.sleep(5);
//		System.out.println(syscache.get("key1").getObjectValue());
//		
//		
//	}

}
