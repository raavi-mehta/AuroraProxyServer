package cachemanagement;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

/**
 * Creates and initializes the caches with all default configurations.
 */
public class CacheStore {
	
	private static CacheManager manager = setupCache();
	
	/**
	 * Creates all the general, location, and image caches.
	 * @return CacheManager that includes all the aforementioned caches.
	 */
	private static CacheManager setupCache() {
		CacheManager manager = CacheManager.create();
		
		// Create location cache
		Cache lcache = new Cache(
		  new CacheConfiguration("lcache", 10000)
		    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
		    .eternal(false)
		    .timeToLiveSeconds(60)
		    .diskExpiryThreadIntervalSeconds(0)
		    .persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));
		
		// Create image cache
		Cache imgcache = new Cache(
		  new CacheConfiguration("imgcache", 10000)
		    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
		    .eternal(false)
		    .timeToLiveSeconds(60)
		    .diskExpiryThreadIntervalSeconds(0)
		    .persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP)));
		 
		// Create general cache
		Cache gcache = new Cache(
				  new CacheConfiguration("gcache", 10000)
				    .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
				    .eternal(false)
				    .timeToLiveSeconds(60)
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
