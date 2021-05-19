package yzkf.app;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang.StringUtils;

import yzkf.config.ConfigFactory;
import yzkf.config.MemcachedConfig;
import yzkf.config.ProjectConfig;
import yzkf.exception.ParserConfigException;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import com.schooner.MemCached.MemcachedItem;
import com.schooner.MemCached.TransCoder;
/**
 * Memcached单例版本<br/>
 * v1.0.1 2011.11.22<br/> 
 * 依赖 whalin-Memcached-Java-Client 2.6.1<br/>
 * v2.0.0 2014.04.03<br/>
 * 依赖 whalin-Memcached-Java-Client 3.0.0<br/>
 * 示例：<br/>
 * <pre>
 * try {
		ConfigFactory factory = ConfigFactory.getInstance();
		MemcachedConfig config = factory.newMemcachedConfig();
		Memcached cache = Memcached.getInstance("CN20120101", config);
		cache.set("test", "Hello world!");
		System.out.println(cache.get("test"));
	} catch (ParserConfigException e) {
		e.printStackTrace();
	}
 * <pre>
 * 使用项目编号获得缓存对象，设置缓存时将会自动添加项目编号作为 key 的前缀，以此来解决项目间的缓存冲突；
 * 如果需要多个项目间共享缓存，则多个项目都使用无参数对象化即可：Memcached cache = Memcached.getInstance();
 * @author qiulw
 *
 */
public class Memcached{
	private static ConcurrentMap<String, Memcached> instances = new ConcurrentHashMap<String, Memcached>();
	public static final String DEFAULT_PREFIX = "yzkf";
	private String poolName;
	private String projectCode;
	private MemCachedClient client;
	/**
	 * 创建Memcached客户端对象
	 * @param projectCode 项目编号
	 * @param poolName 线程池名称
	 */
	protected Memcached(String projectCode,String poolName){
		//super(poolName);
		client = new MemCachedClient(poolName);
		this.projectCode = projectCode == null ? DEFAULT_PREFIX : projectCode.toLowerCase();
		this.poolName = poolName;		
	}	
	/**
	 * 获取Memcached对象
	 * @param projectCode 项目编号，不提供则使用多项目共享缓存
	 * @param config Memcached配置类对象，用于设置链接池，不提供则使用默认配置文件初始化连接池
	 * @return
	 * @throws ParserConfigException
	 */
	public static Memcached getInstance(String projectCode,MemcachedConfig config) throws ParserConfigException{
		String tmpProjectCode = StringUtils.isEmpty(projectCode) ? DEFAULT_PREFIX : projectCode;
		String tmpPoolName = StringUtils.isEmpty(config.getPoolName()) ? DEFAULT_PREFIX : config.getPoolName();
		
		Memcached cache;
		if (!instances.containsKey(tmpProjectCode)){
			synchronized (instances) {
				if (!instances.containsKey(tmpProjectCode)) {					
					cache = new Memcached(tmpProjectCode,tmpPoolName);
					cache.initSocketIOPool(config);
					instances.putIfAbsent(tmpProjectCode, cache);
				}
			}
		}		
		return instances.get(tmpProjectCode);
	}
	/**
	 * 获取使用默认配置文件初始化连接池的Memcached客户端对象 <br>
	 * - 该方法使Memcached与ConfigFactory耦合度增高，不建议使用，只为方便获取Memcached对象
	 * @param projectCode 项目编号
	 * @return
	 * @throws ParserConfigException
	 */
	public static Memcached getInstance(String projectCode) throws ParserConfigException{		
		ConfigFactory factory = ConfigFactory.getInstance();
		MemcachedConfig config = factory.newMemcachedConfig();
		return getInstance(projectCode,config);
	}
	/**
	 * 获取使用默认配置文件初始化连接池的本项目独享Memcached客户端对象 <br>
	 * - 该方法使Memcached与ConfigFactory耦合度增高，不建议使用，只为方便获取Memcached对象
	 * @return
	 * @throws ParserConfigException
	 */
	public static Memcached getInstance() throws ParserConfigException{
		return getInstance(ProjectConfig.getInstance().getCode());
		//return getInstance(DEFAULT_PREFIX);
	}
	/**
	 * 获取公共Memcached客户端对象
	 * @param config Memcached配置类对象，用于设置链接池，不提供则使用默认配置文件初始化连接池
	 * @return
	 * @throws ParserConfigException
	 */
	public static Memcached getInstance(MemcachedConfig config) throws ParserConfigException{
		return getInstance(null,config);
	}
	/**
	 * 初始化连接池
	 * @throws ParserConfigException 
	 */
	private void initSocketIOPool(MemcachedConfig config) throws ParserConfigException{
		SockIOPool pool = SockIOPool.getInstance(this.poolName);
		if(!pool.isInitialized()){
			pool.setServers(config.getServerList());
			pool.setWeights(config.getWeights());
			pool.setFailover(config.isFailover());				
			pool.setInitConn(config.getInitConnections());
			pool.setMinConn(config.getMinConnections());
			pool.setMaxConn(config.getMaxConnections());
			pool.setMaintSleep(config.getMaintenanceSleep());
			pool.setNagle(config.isNagle());
			pool.setSocketConnectTO(config.getSocketConnectTimeout());
			pool.setSocketTO(config.getSocketTimeout());
			//pool.setAliveCheck(true);
			//pool.setMaxIdle( 1000 * 60 * 60 * 6 );
			pool.initialize();
		}
	}
	/**
	 * 为memcached key增加前缀，防止多项目key冲突
	 * @param key
	 * @return
	 */
	private String PrefixKey(String key){
		if(StringUtils.isEmpty(key)) return null;
		return this.projectCode + "_" + key;
	}
	/**
	 * 为memached key增加前缀，防止多项目key冲突
	 * @param keys
	 * @return
	 */
	private String[] PrefixKey(String[] keys){
		if(keys.length == 0) return null;
		String[] prefixKeys = new String[keys.length];
		for(int i=0;i<keys.length;i++){
			prefixKeys[i] = PrefixKey(keys[i]);
		}
		return prefixKeys;
	}
	/**
	 * 检查缓存中是否存在key
	 * 
	 * @param key
	 *            要查找的key
	 * @return true 存在, false 不存在（或已过期）
	 */
	public boolean keyExists(String key) {
		return client.keyExists(PrefixKey(key));
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 *            要删除的key
	 * @return <code>true</code>, 删除成功
	 */
	public boolean delete(String key) {
		return client.delete(PrefixKey(key));
	}

	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return true, 设置成功
	 */
	public boolean set(String key, Object value) {
		return client.set(PrefixKey(key), value);
	}

	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true, 设置成功
	 */
	public boolean set(String key, Object value, Integer hashCode) {
		return client.set(PrefixKey(key), value, hashCode);
	}

	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param expiry
	 *            有效期
	 * @return true, 缓存成功
	 */
	public boolean set(String key, Object value, Date expiry) {
		return client.set(PrefixKey(key), value, expiry);
	}

	/**
	 * 设置缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param expiry
	 *            有效期
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true, 缓存成功
	 */
	public boolean set(String key, Object value, Date expiry, Integer hashCode) {
		return client.set(PrefixKey(key), value, expiry, hashCode);
	}

	/**
	 * 添加缓存，如果已存在key将失败
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return true, 缓存成功
	 */
	public boolean add(String key, Object value) {
		return client.add(PrefixKey(key), value);
	}

	/**
	 * 添加缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true, 缓存成功
	 */
	public boolean add(String key, Object value, Integer hashCode) {
		return client.add(PrefixKey(key), value, hashCode);
	}

	/**
	 * 添加缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param expiry
	 *            有效期
	 * @return true, 缓存成功
	 */
	public boolean add(String key, Object value, Date expiry) {
		return client.add(PrefixKey(key), value, expiry);
	}

	/**
	 * 添加缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param expiry
	 *            有效期
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true, 缓存成功
	 */
	public boolean add(String key, Object value, Date expiry, Integer hashCode) {
		return client.add(PrefixKey(key), value, expiry, hashCode);
	}

	/**
	 * 更新缓存，如果key不存在将失败
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @return true, 缓存成功
	 */
	public boolean replace(String key, Object value) {
		return client.replace(PrefixKey(key), value);
	}

	/**
	 * 更新缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true, 缓存成功
	 */
	public boolean replace(String key, Object value, Integer hashCode) {
		return client.replace(PrefixKey(key), value, hashCode);
	}

	/**
	 * 更新缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param expiry
	 *            有效期
	 * @return true, 缓存成功
	 */
	public boolean replace(String key, Object value, Date expiry) {
		return client.replace(PrefixKey(key), value, expiry);
	}

	/**
	 * 更新缓存
	 * 
	 * @param key
	 *            键
	 * @param value
	 *            值
	 * @param expiry
	 *            有效期
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true, 缓存成功
	 */
	public boolean replace(String key, Object value, Date expiry, Integer hashCode) {
		return client.replace(PrefixKey(key), value, expiry, hashCode);
	}

	/**
	 * 缓存计数器
	 * 
	 * @param key
	 *            键
	 * @param counter
	 *            要存储的长整形
	 * @return true/false 
	 */
	public boolean storeCounter(String key, long counter) {
		return client.storeCounter(PrefixKey(key), new Long(counter));
	}
	/**
	 * 缓存计数器
	 * @param key 键
	 * @param counter 要存储的长整形
	 * @param date 过期时间
	 * @return true/false
	 */
	public boolean storeCounter(String key, Long counter, Date date) {
		return client.storeCounter(PrefixKey(key), counter, date, null);
	}
	/**
	 * 
	 * 缓存计数器
	 * @param key 键
	 * @param counter 要存储的长整形
	 * @param date 过期时间
	 * @param hashCode
	 * @return true/false
	 */
	public boolean storeCounter(String key, Long counter, Date date, Integer hashCode) {
		return client.storeCounter(PrefixKey(key), counter, date, hashCode);
	}
	/**
	 * 缓存计数器
	 * 
	 * @param key
	 *            键
	 * @param counter
	 *            要存储的长整形
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true/false
	 */
	public boolean storeCounter(String key, Long counter, Integer hashCode) {
		return client.storeCounter(PrefixKey(key), counter, hashCode);
	}

	/**
	 * 获取计数器
	 * 
	 * @param key
	 *            键
	 * @return 计数值，如果不存在返回-1
	 */
	public long getCounter(String key) {
		return getCounter(key, null);
	}

	/**
	 * 获取计数器
	 * 
	 * @param key
	 *            键
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return 计数值，如果不存在返回-1
	 */
	public long getCounter(String key, Integer hashCode) {
		return client.getCounter(PrefixKey(key), hashCode);
	}

	/**
	 * 添加计数器，已存在则递增计数器，该方法保证线程安全
	 * 
	 * @param key
	 *            计数器的键
	 * @return 递增后的值
	 */
	public long addOrIncr(String key) {
		return client.addOrIncr(PrefixKey(key));
	}

	/**
	 * 添加计数器，已存在则递增计数器，该方法保证线程安全
	 * 
	 * @param key 计数器的键
	 * @param inc 递增幅度
	 * @return 递增后的值
	 */
	public long addOrIncr(String key, long inc) {
		return client.addOrIncr(PrefixKey(key), inc);
	}
	/**
	 * 添加计数器，已存在则递增计数器，该方法保证线程安全
	 * @param key 计数器的键
	 * @param inc 递增幅度
	 * @param date 计算器过期时间，只有首次添加时有效，已存在时不改变计算器过期时间
	 * @return 递增后的值
	 */
	public long addOrIncr(String key, long inc,Date date) {
		boolean ret = this.add(key, "" + inc, date);

		if (ret) {
			return inc;
		} else {
			return incr(key, inc);
		}
	}

	/**
	 * Thread safe way to initialize and increment a counter.
	 * 
	 * @param key
	 *            key where the data is stored
	 * @param inc
	 *            value to set or increment by
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return value of incrementer
	 */
	public long addOrIncr(String key, long inc, Integer hashCode) {
		return client.addOrIncr(PrefixKey(key), inc, hashCode);
	}

	/**
	 * 递减计数器，不存在则初始化计数器，该方法线程安全
	 * 
	 * @param key
	 *            计数器的键
	 * @return 递减后的值
	 */
	public long addOrDecr(String key) {
		return client.addOrDecr(PrefixKey(key));
	}

	/**
	 * 递减计数器，不存在则初始化计数器，该方法线程安全
	 * 
	 * @param key 计数器的键
	 * @param inc 递减幅度
	 * @return 递减后的值
	 */
	public long addOrDecr(String key, long inc) {
		return client.addOrDecr(PrefixKey(key), inc);
	}
	/**
	 * 递减计数器，不存在则初始化计数器，该方法线程安全
	 * @param key 计数器的键
	 * @param inc 递减幅度
	 * @param date 计算器过期时间，只有首次添加时有效，已存在时不改变计算器过期时间
	 * @return 递减后的值
	 */
	public long addOrDecr(String key, long inc,Date date) {
		boolean ret = this.add(key, "" + inc, date);

		if (ret) {
			return inc;
		} else {
			return decr(key, inc);
		}
	}

	/**
	 * 递减计数器，不存在则初始化计数器，该方法线程安全
	 * 
	 * @param key 计数器的键
	 * @param inc 递减幅度
	 * @param hashCode
	 * @return value of incrementer
	 */
	public long addOrDecr(String key, long inc, Integer hashCode) {
		return client.addOrDecr(PrefixKey(key), inc, hashCode);
	}

	/**
	 * 指定计数器加1，并返回结果
	 * 
	 * @param key
	 *            计数器的键
	 * @return -1, 计算器不能存在
	 */
	public long incr(String key) {
		return client.incr(PrefixKey(key));
	}

	/**
	 * 指定计数器递增指定值，并返回结果
	 * 
	 * @param key
	 *            计数器的键
	 * @param inc
	 *            递增数
	 * @return -1, 计数器不能存在
	 */
	public long incr(String key, long inc) {
		return client.incr(PrefixKey(key), inc);
	}

	/**
	 * Increment the value at the specified key by the specified increment, and
	 * then return it.
	 * 
	 * @param key
	 *            key where the data is stored
	 * @param inc
	 *            how much to increment by
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return -1, if the key is not found, the value after incrementing
	 *         otherwise
	 */
	public long incr(String key, long inc, Integer hashCode) {
		return client.incr(PrefixKey(key), inc, hashCode);
	}

	/**
	 * 指定计数器递减1，并返回结果
	 * 
	 * @param key
	 *            计数器的键
	 * @return -1, 计数器不能存在
	 */
	public long decr(String key) {
		return client.decr(PrefixKey(key));
	}

	/**
	 * 指定计数器递减指定值，并返回结果
	 * 
	 * @param key 计数器的键
	 * @param inc 递减数
	 * @return -1, 计数器不能存在
	 */
	public long decr(String key, long inc) {
		return client.decr(PrefixKey(key), inc);
	}

	/**
	 * 指定计数器递减指定值，并返回结果
	 * 
	 * @param key 计数器的键
	 * @param inc 递减数
	 * @param hashCode if not null, then the int hashcode to use
	* @return -1, 计数器不能存在，否则返回计算器当前值
	 */
	public long decr(String key, long inc, Integer hashCode) {
		return client.decr(PrefixKey(key), inc, hashCode);
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @return 缓存值，不存在则返回null
	 */
	public Object get(String key) {
		return client.get(PrefixKey(key));
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 *            键
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return 缓存值，不存在则返回null
	 */
	public Object get(String key, Integer hashCode) {
		return client.get(PrefixKey(key), hashCode);
	}

	public MemcachedItem gets(String key) {
		return client.gets(PrefixKey(key));
	}

	public MemcachedItem gets(String key, Integer hashCode) {
		return client.gets(PrefixKey(key), hashCode);
	}

	public void setTransCoder(TransCoder transCoder) {
		client.setTransCoder(transCoder);
	}

	/**
	 * Retrieve a key from the server, using a specific hash.
	 * 
	 * If the data was compressed or serialized when compressed, it will
	 * automatically<br/>
	 * be decompressed or serialized, as appropriate. (Inclusive or)<br/>
	 * <br/>
	 * Non-serialized data will be returned as a string, so explicit conversion
	 * to<br/>
	 * numeric types will be necessary, if desired<br/>
	 * 
	 * @param key
	 *            键
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @param asString
	 *            if true, then return string val
	 * @return the object that was previously stored, or null if it was not
	 *         previously stored
	 */
	public Object get(String key, Integer hashCode, boolean asString) {
		return client.get(PrefixKey(key), hashCode, asString);
	}

	/**
	 * 获取多个缓存，返回值数组
	 * 
	 * @param keys
	 *            键数组
	 * @return 与键数组排序相同的值数组
	 */
	public Object[] getMultiArray(String[] keys) {
		return client.getMultiArray(PrefixKey(keys));
	}

	/**
	 * Retrieve multiple objects from the memcache.
	 * 
	 * This is recommended over repeated calls to {@link #get(String) get()},
	 * since it<br/>
	 * is more efficient.<br/>
	 * 
	 * @param keys
	 *            String array of keys to retrieve
	 * @param hashCodes
	 *            if not null, then the Integer array of hashCodes
	 * @return Object array ordered in same order as key array containing
	 *         results
	 */
	public Object[] getMultiArray(String[] keys, Integer[] hashCodes) {
		return client.getMultiArray(PrefixKey(keys), hashCodes);
	}

	/**
	 * Retrieve multiple objects from the memcache.
	 * 
	 * This is recommended over repeated calls to {@link #get(String) get()},
	 * since it<br/>
	 * is more efficient.<br/>
	 * 
	 * @param keys
	 *            String array of keys to retrieve
	 * @param hashCodes
	 *            if not null, then the Integer array of hashCodes
	 * @param asString
	 *            if true, retrieve string vals
	 * @return Object array ordered in same order as key array containing
	 *         results
	 */
	public Object[] getMultiArray(String[] keys, Integer[] hashCodes, boolean asString) {
		return client.getMultiArray(PrefixKey(keys), hashCodes, asString);
	}

	/**
	 * Retrieve multiple objects from the memcache.
	 * 
	 * This is recommended over repeated calls to {@link #get(String) get()},
	 * since it<br/>
	 * is more efficient.<br/>
	 * 
	 * @param keys
	 *            String array of keys to retrieve
	 * @return a hashmap with entries for each key is found by the server, keys
	 *         that are not found are not entered into the hashmap, but
	 *         attempting to retrieve them from the hashmap gives you null.
	 */
	public Map<String, Object> getMulti(String[] keys) {
		return getMulti(keys, null);
	}

	/**
	 * Retrieve multiple keys from the memcache.
	 * 
	 * This is recommended over repeated calls to {@link #get(String) get()},
	 * since it<br/>
	 * is more efficient.<br/>
	 * 
	 * @param keys
	 *            keys to retrieve
	 * @param hashCodes
	 *            if not null, then the Integer array of hashCodes
	 * @return a hashmap with entries for each key is found by the server, keys
	 *         that are not found are not entered into the hashmap, but
	 *         attempting to retrieve them from the hashmap gives you null.
	 */
	public Map<String, Object> getMulti(String[] keys, Integer[] hashCodes) {
		return client.getMulti(PrefixKey(keys), hashCodes);
	}

	/**
	 * Retrieve multiple keys from the memcache.
	 * 
	 * This is recommended over repeated calls to {@link #get(String) get()},
	 * since it<br/>
	 * is more efficient.<br/>
	 * 
	 * @param keys
	 *            keys to retrieve
	 * @param hashCodes
	 *            if not null, then the Integer array of hashCodes
	 * @param asString
	 *            if true then retrieve using String val
	 * @return a hashmap with entries for each key is found by the server, keys
	 *         that are not found are not entered into the hashmap, but
	 *         attempting to retrieve them from the hashmap gives you null.
	 */
	public Map<String, Object> getMulti(String[] keys, Integer[] hashCodes, boolean asString) {
		return client.getMulti(PrefixKey(keys), hashCodes, asString);
	}

	public boolean sync(String key, Integer hashCode) {
		return client.sync(PrefixKey(key), hashCode);
	}

	public boolean sync(String key) {
		return client.sync(PrefixKey(key));
	}

	public boolean append(String key, Object value, Integer hashCode) {
		return client.append(PrefixKey(key), value, hashCode);
	}

	public boolean append(String key, Object value) {
		return client.append(PrefixKey(key), value);
	}

	public boolean cas(String key, Object value, Integer hashCode, long casUnique) {
		return client.cas(PrefixKey(key), value, hashCode, casUnique);
	}

	public boolean cas(String key, Object value, Date expiry, long casUnique) {
		return client.cas(PrefixKey(key), value, expiry, casUnique);
	}

	public boolean cas(String key, Object value, Date expiry, Integer hashCode, long casUnique) {
		return client.cas(PrefixKey(key), value, expiry, hashCode, casUnique);
	}

	public boolean cas(String key, Object value, long casUnique) {
		return client.cas(PrefixKey(key), value, casUnique);
	}

	public boolean prepend(String key, Object value, Integer hashCode) {
		return client.prepend(PrefixKey(key), value, hashCode);
	}

	public boolean prepend(String key, Object value) {
		return client.prepend(PrefixKey(key), value);
	}
}
