package cachemanagement;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;

import com.mashape.unirest.http.HttpResponse;

import net.sf.ehcache.Element;

/**
 * Adds entries to the caches and sets the lifespans of each cache.
 */
public class CacheUpdater {
	
	/**
	 * Add location response to the cache.
	 * @param userQuery
	 * 		Query that acts as the key to the cache
	 * @param res
	 * 		Response to be stored
	 */
	public static void addLocactiontoCache(String userQuery, Response res){
		CacheStore.getCacheManager().getCache("lcache")
			.put(new Element(userQuery, res));
	}
	
	/**
	 * Add image response to the cache.
	 * @param userQuery
	 * 		Query that acts as the key to the cache
	 * @param res
	 * 		Response to be stored
	 */
	public static void addImgtoCache(String userQuery, HttpResponse<InputStream> imgR){
		byte[] img = null;
		try {
			img = new byte[imgR.getBody().available()];
			imgR.getBody().read(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
		CacheStore.getCacheManager().getCache("imgcache")
		.put(new Element(userQuery,img));
	}
	
	/**
	 * Add general response to the cache.
	 * @param userQuery
	 * 		Query that acts as the key to the cache
	 * @param res
	 * 		Response to be stored
	 */
	public static void addGeneraltoCache(String userQuery, Response res){
		CacheStore.getCacheManager().getCache("gcache")
		.put(new Element(userQuery,res));
	}
	
	/**
	 * Sets the lifespan for location queries in the cache.
	 * @param lifespan
	 * 		lifespan in seconds
	 */
	public static void setLocationLifespan(int lifespan) {
		CacheStore.getCacheManager().getCache("lcache")
		.getCacheConfiguration().timeToLiveSeconds(lifespan);
	}
	
	/**
	 * Sets the lifespan for image queries in the cache.
	 * @param lifespan
	 * 		lifespan in seconds
	 */
	public static void setImageLifespan(int lifespan) {
		CacheStore.getCacheManager().getCache("imgcache")
		.getCacheConfiguration().timeToLiveSeconds(lifespan);
	}
	
	/**
	 * Sets the lifespan for general queries in the cache.
	 * @param lifespan
	 * 		lifespan in seconds
	 */
	public static void setGeneralLifespan(int lifespan) {
		CacheStore.getCacheManager().getCache("gcache")
		.getCacheConfiguration().timeToLiveSeconds(lifespan);
	}
}
