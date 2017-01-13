package com.soch.uam.util;

import org.jboss.cache.pojo.PojoCache;
import org.jboss.cache.pojo.PojoCacheFactory;

public class CacheUtil {
	
	PojoCache cache = null;
	String configFile = "META-INF/replSync-service.xml";
	
	private static CacheUtil cacheUtil = null;
	
	public Object getCache(String name) 
	{
		return cache.find(name);
	// This will start PojoCache automatically
	}
	
	public void detachCache(String name) 
	{
		cache.detach(name);
	// This will start PojoCache automatically
	}
	
	public void putCache(String name, Object value) 
	{
		cache.attach(name, value);
	// This will start PojoCache automatically
	}
	
	private CacheUtil() {
		cache = PojoCacheFactory.createCache(configFile); 
	}
	
	public static CacheUtil getInstance()
	{
		if(cacheUtil == null)
		{
			cacheUtil = new CacheUtil();
		}
		
		return cacheUtil;
	}

}
