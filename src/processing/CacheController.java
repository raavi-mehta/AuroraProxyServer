package processing;

import java.io.InputStream;

import javax.ws.rs.core.Response;

import org.json.JSONException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

import cachemanagement.CacheChecker;
import cachemanagement.CacheStore;
import cachemanagement.CacheUpdater;
import net.sf.ehcache.CacheManager;

public class CacheController {
	
	private static int hits = 0;
	private static int misses = 0;

	public static Response cacheProcess(String userQuery) throws JSONException, UnirestException {
		
		if (isImageRequest(userQuery)) 
			return ProcessImageCacheQuery(userQuery);
		
		else if(userQuery.contains("type=locations"))
			return ProcessLocationCacheQuery(userQuery);
		
		else 
			return ProcessGeneralCacheQuery(userQuery);
		
	}
	
	public static Response ProcessImageCacheQuery(String userQuery) throws JSONException, UnirestException{
		try{

			Response x = CacheChecker.ImageCacheCheck(userQuery);
			System.out.println("IMG: Found in Cache");
			hits++;
			return x;
			
		}catch(NullPointerException e){
			
			HttpResponse<InputStream> imgR = DataRetriever.FetchAuroraImages(userQuery);

			if (imgR.getStatus() == 200) 
				CacheUpdater.addImgtoCache(userQuery, imgR);
			else 
				return GenerateInvalidResponse(imgR);
			
			System.out.println("IMG: Not in Cache");
			misses++;
			return GenerateImageResponse(userQuery);

		}
	}
	
	public static Response ProcessLocationCacheQuery(String userQuery) throws JSONException, UnirestException{
		try{
			Response x = CacheChecker.LocationCacheCheck(userQuery);
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
	
	public static Response ProcessGeneralCacheQuery(String userQuery) throws JSONException, UnirestException{
		try{
			Response x = CacheChecker.GeneralCacheCheck(userQuery);
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
	
	public static Response GenerateInvalidResponse(HttpResponse<InputStream> imgR){

		return Response.status(imgR.getStatus())
				.entity(imgR.getBody()).type("application/json")
				.build();
	}
	public static Response GenerateImageResponse(String userQuery){

		return Response.status(200)
				.entity(CacheStore.getCacheManager().getCache("imgcache").get(userQuery).getObjectValue())
				.type("image/jpeg")
				.build();
	}
	
	public static int getHits() {
		return hits;
	}
	
	public static int getMisses() {
		return misses;
	}
	
	public static boolean isImageRequest(String userQuery){
		return (userQuery.contains("type=embed") || userQuery.contains("type=images&image")
		||userQuery.contains("type=map") || userQuery.contains("type=gmap"));
	}
	
	public static void clearCache() {
		hits = 0;
		misses = 0;
		CacheStore.getCacheManager().clearAll();
	}
	
	public static void setLocationLifespan(int lifespan) {
		CacheUpdater.setLocationLifespan(lifespan);
	}
	
	public static void setImageLifespan(int lifespan) {
		CacheUpdater.setImageLifespan(lifespan);
	}
	
	public static void setGeneralLifespan(int lifespan) {
		CacheUpdater.setGeneralLifespan(lifespan);
	}
	
	public static String getLifespans() {
		return 	"general lifespan: " + getCacheManager().getCache("gcache").getCacheConfiguration()
				.getTimeToLiveSeconds() + System.lineSeparator() +
				"location lifespan: " + getCacheManager().getCache("lcache").getCacheConfiguration()
				.getTimeToLiveSeconds() + System.lineSeparator() +
				"image lifespan: " + getCacheManager().getCache("imgcache").getCacheConfiguration()
				.getTimeToLiveSeconds() + System.lineSeparator();
	}
	
	public static CacheManager getCacheManager() {
		return CacheStore.getCacheManager();
	}
	
}
