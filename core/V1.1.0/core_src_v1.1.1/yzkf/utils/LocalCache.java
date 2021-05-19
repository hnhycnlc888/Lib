package yzkf.utils;

import org.apache.commons.lang.StringUtils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 本地缓存
 * <p>
 * 使用ehcache
 * </p>
 * @author qiulw
 * @version V4.0.0 2011.11.25
 */
public class LocalCache {
	private static final String PATH_DEFAULT = "/ehcache.xml"; 
	private static LocalCache instance;	
	private Cache ehcache;
	private String projectCode;
	
	LocalCache(){
		this(null,null);
	}
	LocalCache(String configurationFileName){
		this(null,configurationFileName);
	}
	LocalCache(String projectCode,String configurationFileName){
		this.projectCode  = projectCode == null ? "yzkf" : projectCode.toLowerCase();
		CacheManager manager = null;
		if(configurationFileName == null){
			manager = CacheManager.create(LocalCache.class.getResource(PATH_DEFAULT));
		}
		else
			manager = CacheManager.create(configurationFileName);
		//ehcache = manager.getCache("yzkf");
		ehcache = manager.getCache(this.projectCode);
	}
	/**
	 * 为LocalCache key增加前缀，防止多项目key冲突
	 * @param key
	 * @return
	 */
	private String PrefixKey(String key){
		if(StringUtils.isEmpty(key)) return null;
		//return this.projectCode + "_" + key;
		return key;
	}
	/**
	 * 获得默认配置的单例
	 * @return
	 */
	public static LocalCache getInstance(){
		if(instance == null){
			instance = new LocalCache();
		}
		return instance;
	}
	/**
	 * 获取缓存
	 * @param key 键
	 * @return 值
	 */
	public Object get(String key){
		Element element = this.ehcache.get(PrefixKey(key));
		if(element == null)
			return null;	
		return element.getObjectValue();		
	}
	/**
	 * 设置缓存
	 * @param key 键
	 * @param value 值
	 */
	public void set(String key,Object value){
		this.ehcache.put(new Element(PrefixKey(key),value));
	}
	/**
	 * 设置缓存
	 * @param key 键
	 * @param value 值
	 * @param timeToLiveSeconds 有效期，单位秒
	 */
	public void set(String key,Object value,int timeToLiveSeconds){
		Element elem = new Element(PrefixKey(key),value);
		elem.setTimeToLive(timeToLiveSeconds);
		this.ehcache.put(elem);
	}
	/**
	 * 移除缓存
	 * @param key 键
	 * @return
	 */
	public boolean remove(String key){
		return this.ehcache.remove(PrefixKey(key));
	}
	/**
	 * 移除所有缓存
	 */
	public void removeAll(){
		this.ehcache.removeAll();
	}
	/**
	 * 获取缓存，静态方法
	 * @param key 键
	 * @return
	 */
	public static Object getCache(String key){
		return getInstance().get(key);
	}
	/**
	 * 设置缓存，静态方法
	 * @param key 键
	 * @param value 值
	 */
	public static void setCache(String key,Object value){
		getInstance().set(key, value);
	}
	/**
	 * 设置缓存，静态方法
	 * @param key 键
	 * @param value 值
	 * @param timeToLiveSeconds 有效期，单位秒
	 */
	public static void setCache(String key,Object value,int timeToLiveSeconds){
		getInstance().set(key, value,timeToLiveSeconds);
	}
	/**
	 * 异常缓存
	 * @param key 键
	 * @return
	 */
	public static boolean removeCache(String key){
		return getInstance().remove(key);
	}
	/**
	 * 移除所有缓存
	 */
	public static void removeAllCache(){
		getInstance().removeAll();
	}
}
 