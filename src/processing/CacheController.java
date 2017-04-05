package processing;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;

import org.json.JSONException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

import cachemanagement.CacheStore;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class CacheController {
	
	private static int hits = 0;
	private static int misses = 0;
	

	public static Response cacheProcess(String userQuery) throws JSONException, UnirestException {
		
		CacheManager manager = CacheStore.getCacheManager();
		
		if (isImageRequest(userQuery)) {
			
			
			HttpResponse<InputStream> imgR = null;
			byte[] array = null;
			try{
				array = (byte[]) manager.getCache("imgcache").get(userQuery).getObjectValue();
				System.out.println("IMG: Found in Cache");
				hits++;
			}catch(NullPointerException e){
				imgR = DataRetriever.FetchAuroraImages(userQuery);
				try {
					array = new byte[imgR.getBody().available()];
					imgR.getBody().read(array);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if (imgR.getStatus() == 200) 
					manager.getCache("imgcache").put(new Element(userQuery,array));

				System.out.println("IMG: Not in Cache");
				misses++;
				
			}
			
			return Response.status(200)
					.entity(manager.getCache("imgcache").get(userQuery).getObjectValue())
					.type("image/jpeg")
					.build();
			
			
		} else if(userQuery.contains("type=locations")){
			
			
			
			Cache syscache = manager.getCache("lcache");
			try{
				Response x = (Response) syscache.get(userQuery).getObjectValue();
				System.out.println("JSON: Found in Cache");
				hits++;
				return x;
			}catch(NullPointerException e){
				System.out.println("JSON: Not in Cache");
				misses++;
				Response res = DataRetriever.FetchAuroraLocations(userQuery);
				if (res.getStatus() == 200) 
					syscache.put(new Element(userQuery,res));
				return res;
			}
			
			
			
			
		} else {
			
			
			
			Cache syscache = manager.getCache("gcache");
			try{
				Response x = (Response) syscache.get(userQuery).getObjectValue();
				System.out.println("JSON: Found in Cache");
				hits++;
				return x;
			}catch(NullPointerException e){
				System.out.println("JSON: Not in Cache");
				misses++;
				Response res = DataRetriever.FetchAuroraGeneral(userQuery);
				if (res.getStatus() == 200) 
					syscache.put(new Element(userQuery,res));
				return res;
			}
			
			
		}
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
	
}
