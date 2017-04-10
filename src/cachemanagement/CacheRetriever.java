package cachemanagement;

import javax.ws.rs.core.Response;

/**
 * Retrieves responses from the appropriate cache as specified in the user
 * query.
 */
public class CacheRetriever {
	
	/**
	 * Retrieves the specified image per the user query from the cache.
	 * @param userQuery
	 * 		User query specifying the image to retrieve
	 * @return
	 * 		Image response
	 */
	public static Response ImageCacheRetriever(String userQuery){
		byte[] img = (byte[]) CacheStore.getCacheManager().getCache("imgcache")
				.get(userQuery).getObjectValue();
		return Response.status(200)
				.entity(img)
				.type("image/jpeg")
				.build();
	}
	
	/**
	 * Retrieves the specified general response per the user query from the
	 * cache.
	 * @param userQuery
	 * 		User query specifying the general response to retrieve
	 * @return
	 * 		General response
	 */
	public static Response GeneralCacheRetriever(String userQuery){
		return (Response) CacheStore.getCacheManager().getCache("gcache")
				.get(userQuery).getObjectValue();
	}
	
	/**
	 * Retrieves the specified location response per the user query from the
	 * cache.
	 * @param userQuery
	 * 		User query specifying the location response to retrieve
	 * @return
	 * 		Location response
	 */
	public static Response LocationCacheRetriever(String userQuery){
		return (Response) CacheStore.getCacheManager().getCache("lcache")
				.get(userQuery).getObjectValue();
	}
}
