package yzkf.config;

import org.apache.commons.lang.StringUtils;

import yzkf.exception.ParserConfigException;
import yzkf.utils.TryParse;

/**
 * Memcached 配置信息
 * v1.0.0 2011.11.14
 * @author qiulw
 *
 */
public class MemcachedConfig extends yzkf.config.Configuration {
	private final String PATH_POOLNAME = "/memcached/poolName";
	private final String PATH_FAILOVER = "/memcached/failover";
	private final String PATH_MAINTSLEEP = "/memcached/maintenancesleep";
	private final String PATH_MAXCONN = "/memcached/maxconnections";
	private final String PATH_NAGLE = "/memcached/nagle";	
	private final String PATH_SOCKETCONNECTTO = "/memcached/socketconnecttimeout";
	private final String PATH_SOCKETTO = "/memcached/sockettimeout";
	private final String PATH_SERVERS = "/memcached/serverlist";	
	//private static MemcachedConfig instance;
	private boolean failover = false;
	private int initConnections = 5;	
	private int minConnections = 5;
	private int maintenanceSleep = 30;
	private int maxConnections = 250;
	private boolean nagle = false;
	private String[] serverList = {"192.168.18.109:4458"};
	private int socketConnectTimeout = 2000;
	private int socketTimeout = 3000;
	private Integer[] weights;
	private String poolName;
	
	MemcachedConfig(String path) throws ParserConfigException {
		super(path);
		poolName=getXPathValue(PATH_POOLNAME);
		failover = TryParse.toBoolean(getXPathValue(PATH_FAILOVER));
		initConnections = 5;	
		minConnections = 5;
		maintenanceSleep = TryParse.toInt(getXPathValue(PATH_MAINTSLEEP));
		maxConnections = TryParse.toInt(getXPathValue(PATH_MAXCONN));
		nagle = TryParse.toBoolean(getXPathValue(PATH_NAGLE));			
		socketConnectTimeout = TryParse.toInt(getXPathValue(PATH_SOCKETCONNECTTO));
		socketTimeout = TryParse.toInt(getXPathValue(PATH_SOCKETTO));
		String tmpString = getXPathValue(PATH_SERVERS);
		if(StringUtils.isEmpty(tmpString))
			throw new ParserConfigException("Memcached 配置 Servers 不能为空.");
		serverList = tmpString.split(",");
		if(serverList.length >= 2){
			tmpString = getXPathValue("/memcached/weights");
			if(StringUtils.isEmpty(tmpString)){
				String[] tmpArray = tmpString.split(",");
				if(tmpArray.length != serverList.length)
					throw new ParserConfigException("Memcached 配置 Weights 与 Servers 不匹配.");
				weights = new Integer[tmpArray.length];
				for(int i=0;i<tmpArray.length;i++){
					weights[i] = TryParse.toInt(tmpArray[i]);
				}
			}
		}
	}	
//	public static MemcachedConfig getInstance(String path) throws ParserConfigException{
//		if(instance == null)
//			instance = new MemcachedConfig(path);
//		return instance;
//	}
	

	/**
	 * 联机失败是否自动切换服务器，为true时将自动切换服务器，否则不切换返回null
	 * @return
	 */
	public boolean isFailover() {
		return failover;
	}
	/**
	 * 维护线程的休眠时间（毫秒）,如果为 0 ，则维护线程不启动
	 * @return
	 */
	public int getMaintenanceSleep() {
		return maintenanceSleep;
	}
	/**
	 * 获取初始的每台服务器的可用连接数量。
	 * @return
	 */
	public int getInitConnections() {
		return initConnections;
	}
	/**
	 * 获取连接池中最小的可用空闲链接数
	 * @return
	 */
	public int getMinConnections() {
		return minConnections;
	}
	/**
	 * 获取连接池的最大连接数（默认的最大连接1024）
	 * @return
	 */
	public int getMaxConnections() {
		return maxConnections;
	}
	/**
	 * 算法标识，是否用nagle算法启动socket
	 * @return
	 */
	public boolean isNagle() {
		return nagle;
	}
	/**
	 * 获取服务器列表，多个以逗号分隔。如：192.168.3.234:11211,192.168.3.234:11211
	 * @return
	 */
	public String[] getServerList() {
		return serverList;
	}
	/**
	 * 获取socket连接超时时间（毫秒）
	 * @return
	 */
	public int getSocketConnectTimeout() {
		return socketConnectTimeout;
	}
	/**
	 * 获取socket读取的超时时间（毫秒）
	 * @return
	 */
	public int getSocketTimeout() {
		return socketTimeout;
	}
	/**
	 * 服务器的负载量。与ServerList对应。以逗号分隔。如：3,2
	 * @return
	 */
	public Integer[] getWeights() {
		return weights;
	}
	/**
	 * 获取线程池名称
	 * @return
	 */
	public String getPoolName() {
		return poolName;
	}
}
