package cachemanagement;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Response;

import com.mashape.unirest.http.HttpResponse;

import net.sf.ehcache.Element;

public class CacheUpdater {
	
	public static void addLocactiontoCache(String userQuery, Response res){
		CacheStore.getCacheManager().getCache("lcache")
		.put(new Element(userQuery,res));
	}
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

	public static void addGeneraltoCache(String userQuery, Response res){
		CacheStore.getCacheManager().getCache("gcache")
		.put(new Element(userQuery,res));
	}
	public static void setLocationLifespan(int lifespan) {
		CacheStore.getCacheManager().getCache("lcache")
		.getCacheConfiguration().timeToLiveSeconds(lifespan);
	}
	
	public static void setImageLifespan(int lifespan) {
		CacheStore.getCacheManager().getCache("imgcache")
		.getCacheConfiguration().timeToLiveSeconds(lifespan);
	}
	
	public static void setGeneralLifespan(int lifespan) {
		CacheStore.getCacheManager().getCache("gcache")
		.getCacheConfiguration().timeToLiveSeconds(lifespan);
	}
}
