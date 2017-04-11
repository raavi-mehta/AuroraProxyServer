package processing;

import java.io.InputStream;

import javax.ws.rs.core.Response;

import org.json.JSONException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

import cachemanagement.CacheRetriever;
import cachemanagement.CacheStore;
import cachemanagement.CacheUpdater;
import net.sf.ehcache.CacheManager;

/**
 * Processes cache requests and generates responses as appropriate.
 */
public class CacheController {
	
	private static int hits = 0;
	private static int misses = 0;
	
	/**
	 * Determines the query type and handles it accordingly.
	 * @param userQuery
	 * 		Query to be processed.
	 * @return
	 * 		The appropriate response object contained within the cache.
	 */
	public static Response cacheProcess(String userQuery) throws JSONException, UnirestException {
		
		if (isImageRequest(userQuery)) 
			return ProcessImageCacheQuery(userQuery);
		
		else if(userQuery.contains("type=locations"))
			return ProcessLocationCacheQuery(userQuery);
		
		else 
			return ProcessGeneralCacheQuery(userQuery);
		
	}
	
	/**
	 * Checks if the image exists in the cache. If it does, returns the image
	 * from the cache. Else fetches the image from Aurora servers.
	 * @param userQuery
	 * 		Image query to process
	 * @return
	 * 		Image response object
	 */
	public static Response ProcessImageCacheQuery(String userQuery) throws JSONException, UnirestException{
		try{

			Response x = CacheRetriever.ImageCacheRetriever(userQuery);
			System.out.println("IMG: Found in Cache");
			hits++;
			return x;
			
		}catch(NullPointerException e){
			misses++;
			HttpResponse<InputStream> imgR = DataRetriever.FetchAuroraImages(userQuery);

			if (imgR.getStatus() == 200) 
				CacheUpdater.addImgtoCache(userQuery, imgR);
			else 
				return GenerateInvalidResponse(imgR);
			
			System.out.println("IMG: Not in Cache");
			
			return GenerateImageResponse(userQuery);

		}
	}
	
	/**
	 * Checks if the location response exists in the cache. If it does, returns
	 * the response from the cache. Else fetches the response from Aurora servers.
	 * @param userQuery
	 * 		location query to process
	 * @return
	 * 		location response object
	 */
	public static Response ProcessLocationCacheQuery(String userQuery) throws JSONException, UnirestException{
		try{
			Response x = CacheRetriever.LocationCacheRetriever(userQuery);
			System.out.println("JSON: Found in Cache");
			hits++;
			return x;
		}catch(NullPointerException e){
			System.out.println("JSON: Not in Cache");
			misses++;
			Response res = DataRetriever.FetchAuroraLocations(userQuery);
			if (res.getStatus() == 200) 
				CacheUpdater.addLocactiontoCache(userQuery, res);;
			return res;
		}
	}
	
	/**
	 * Checks if the general response exists in the cache. If it does, returns
	 * the response from the cache. Else fetches the response from Aurora servers.
	 * @param userQuery
	 * 		general query to process
	 * @return
	 * 		general response object
	 */
	public static Response ProcessGeneralCacheQuery(String userQuery) throws JSONException, UnirestException{
		try{
			Response x = CacheRetriever.GeneralCacheRetriever(userQuery);
			System.out.println("JSON: Found in Cache");
			hits++;
			return x;
		}catch(NullPointerException e){
			System.out.println("JSON: Not in Cache");
			misses++;
			Response res = DataRetriever.FetchAuroraGeneral(userQuery);
			if (res.getStatus() == 200) 
				CacheUpdater.addGeneraltoCache(userQuery, res);
			return res;
		}
	}
	
	/**
	 * Returns a hardcoded invalid response.
	 */
	public static Response GenerateInvalidResponse(HttpResponse<InputStream> imgR){
		return Response.status(imgR.getStatus())
				.entity(imgR.getBody()).type("application/json")
				.build();
	}
	
	/**
	 * Creates a response from the image in the cache.
	 * @param userQuery
	 * 		Used to retrieve the appropriate image object.
	 * @return
	 * 		Image response object.
	 */
	public static Response GenerateImageResponse(String userQuery){
		return Response.status(200)
				.entity(CacheStore.getCacheManager().getCache("imgcache").get(userQuery).getObjectValue())
				.type("image/jpeg")
				.build();
	}
	
	/**
	 * Returns the number of hits
	 * @return
	 * 		Number of hits
	 */
	public static int getHits() {
		return hits;
	}
	
	/**
	 * Returns the number of misses
	 * @return
	 * 		Number of misses
	 */
	public static int getMisses() {
		return misses;
	}
	
	/**
	 * Helper function that returns whether the query is an image query.
	 * @param userQuery
	 * 		Query to analyze
	 * @return
	 * 		True if it is an image query, false if it is not.
	 */
	public static boolean isImageRequest(String userQuery){
		return (userQuery.contains("type=embed") || userQuery.contains("type=images&image")
		||userQuery.contains("type=map") || userQuery.contains("type=gmap"));
	}
	
	/**
	 * Clears the cache and resets the hits and misses counters.
	 */
	public static void clearCache() {
		hits = 0;
		misses = 0;
		CacheStore.getCacheManager().clearAll();
	}
	
	/**
	 * Sets the location query lifespan within the cache.
	 * @param lifespan
	 * 		lifespan in seconds
	 */
	public static void setLocationLifespan(int lifespan) {
		CacheUpdater.setLocationLifespan(lifespan);
	}
	
	/**
	 * Sets the image query lifespan within the cache.
	 * @param lifespan
	 * 		lifespan in seconds
	 */
	public static void setImageLifespan(int lifespan) {
		CacheUpdater.setImageLifespan(lifespan);
	}
	
	/**
	 * Sets the general query lifespan within the cache.
	 * @param lifespan
	 * 		lifespan in seconds
	 */
	public static void setGeneralLifespan(int lifespan) {
		CacheUpdater.setGeneralLifespan(lifespan);
	}
	
	/**
	 * Returns the current lifespans as a string.
	 * @return
	 * 		Current lifespans of all caches as a string.
	 */
	public static String getLifespans() {
		return 	"general lifespan: " + getCacheManager().getCache("gcache").getCacheConfiguration()
					.getTimeToLiveSeconds() + System.lineSeparator() +
				"location lifespan: " + getCacheManager().getCache("lcache").getCacheConfiguration()
					.getTimeToLiveSeconds() + System.lineSeparator() +
				"image lifespan: " + getCacheManager().getCache("imgcache").getCacheConfiguration()
					.getTimeToLiveSeconds() + System.lineSeparator();
	}
	
	/**
	 * Retrieves the CacheManager object for all the caches.
	 * @return
	 * 		CacheManager object
	 */
	public static CacheManager getCacheManager() {
		return CacheStore.getCacheManager();
	}
	
}
