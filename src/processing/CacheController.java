package processing;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class CacheController {
	
	private static CacheManager manager = setupCache();
	
	@SuppressWarnings("unchecked")
	public static Response cacheProcess(UriInfo uriInfo) throws JSONException, UnirestException {
		
		String userQuery = uriInfo.getRequestUri().getQuery();
		
		if (isImageRequest(userQuery)) {
			HttpResponse<InputStream> imgR = null;
			byte[] array = null;
			try{
				imgR = (HttpResponse<InputStream>) manager.getCache("syscache").get(userQuery).getObjectValue();
				array = (byte[]) manager.getCache("imgcache").get(userQuery).getObjectValue();
				System.out.println("IMG: Found in Cache");
			}catch(NullPointerException e){
				imgR = DataRetriever.FetchAuroraImages(uriInfo);
				try {
					array = new byte[imgR.getBody().available()];
					imgR.getBody().read(array);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				manager.getCache("imgcache").put(new Element(userQuery,array));
				manager.getCache("syscache").put(new Element(userQuery,imgR));
				System.out.println("IMG: Not in Cache");
			}
			return Response.status(imgR.getStatus())
					.entity(manager.getCache("imgcache").get(userQuery).getObjectValue())
					.type("image/jpeg")
					.build();
//			HttpResponse<InputStream> imgR = DataRetriever.FetchAuroraImages(uriInfo);
////			byte [] array = null;
////			try {
////				array = new byte[imgR.getBody().available()];
////				imgR.getBody().read(array);
////			} catch (IOException e) {
////				e.printStackTrace();
////			}
//			manager.getCache("imgcache").put(new Element(userQuery,imgR));
			

		} else {
			Cache syscache = manager.getCache("syscache");
			try{
				Response x = (Response) syscache.get(userQuery).getObjectValue();
				System.out.println("JSON: Found in Cache");
				return x;
			}catch(NullPointerException e){
				System.out.println("JSON: Not in Cache");
				Response res = DataRetriever.FetchAuroraJson(uriInfo);
				syscache.put(new Element(userQuery,res));
				return res;
			}
			
			
		}
	}
	
	
	public static boolean isImageRequest(String userQuery){
		return (userQuery.contains("type=embed") || userQuery.contains("type=images&image")
		||userQuery.contains("type=map") || userQuery.contains("type=gmap"));
	}
	
	
	
	
	
	
	
	private static CacheManager setupCache() {
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
		
		Cache imgcache = new Cache(
		  new CacheConfiguration("imgcache", 10000)
		    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
		    .eternal(false)
		    .timeToLiveSeconds(60)
		    .timeToIdleSeconds(30)
		    .diskExpiryThreadIntervalSeconds(0)
		    .persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));
		 
		manager.addCache(syscache);
		manager.addCache(imgcache);
		return manager;
	}
	
	
	public static CacheManager getCacheManager() {
		return manager;
	}
	
}
