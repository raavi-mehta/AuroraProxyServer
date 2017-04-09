package cachemanagement;

import javax.ws.rs.core.Response;


public class CacheChecker {
	
	public static Response ImageCacheCheck(String userQuery){
		byte[] img = (byte[]) CacheStore.getCacheManager().getCache("imgcache")
				.get(userQuery).getObjectValue();
		return Response.status(200)
				.entity(img)
				.type("image/jpeg")
				.build();
	}
	
	public static Response GeneralCacheCheck(String userQuery){
		return (Response) CacheStore.getCacheManager().getCache("gcache")
				.get(userQuery).getObjectValue();
	}
	public static Response LocationCacheCheck(String userQuery){
		return (Response) CacheStore.getCacheManager().getCache("lcache")
				.get(userQuery).getObjectValue();
	}
}
