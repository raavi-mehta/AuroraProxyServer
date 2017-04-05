package cachemanagement;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class CacheStore {
	
	private static CacheManager manager = setupCache();
	
	private static CacheManager setupCache() {
		//Create a Cache specifying its configuration.
		CacheManager manager = CacheManager.create();
		Cache lcache = new Cache(
		  new CacheConfiguration("lcache", 10000)
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
		    .timeToLiveSeconds(10)
		    .timeToIdleSeconds(0)
		    .diskExpiryThreadIntervalSeconds(0)
		    .persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));
		 
		Cache gcache = new Cache(
				  new CacheConfiguration("gcache", 10000)
				    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
				    .eternal(false)
				    .timeToLiveSeconds(1)
				    .timeToIdleSeconds(0)
				    .diskExpiryThreadIntervalSeconds(0)
				    .persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));
		
		manager.addCache(lcache);
		manager.addCache(gcache);
		manager.addCache(imgcache);
		return manager;
	}
	
	public static CacheManager getCacheManager() {
		return manager;
	}
	
}
