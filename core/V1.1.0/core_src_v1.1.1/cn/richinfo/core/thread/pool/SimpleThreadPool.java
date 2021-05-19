package cn.richinfo.core.thread.pool;

import java.io.Closeable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.InitializingBean;

public class SimpleThreadPool implements InitializingBean, Closeable {
	
	private static final AtomicInteger mThreadNum = new AtomicInteger(1);
	private ExecutorService es = null;
	private boolean daemon = false;
	private int poolSize = 10;
	

	@Override
	public void afterPropertiesSet() throws Exception {
		ThreadFactory tf = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				String name = "pool-thread-" + mThreadNum.getAndIncrement();
				Thread thread = new Thread(r, name);
				thread.setDaemon(daemon);
				return thread;
			}
		};
		ExecutorService es = Executors.newFixedThreadPool(poolSize, tf);
		this.es = es;
	}
	
	public void execute(Runnable run){
		es.execute(run);
	}
	
	public <T> Future<T> submit(Callable<T> call){
		return es.submit(call);
	}
	
	@Override
	public void close(){
		if(es != null){
			es.shutdownNow();
		}
	}

	public int getPoolSize() {
		return poolSize;
	}
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	public void setDaemon(boolean daemon) {
		this.daemon = daemon;
	}
	
}
