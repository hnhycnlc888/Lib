package cn.richinfo.core.spring;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

public class SpringContext implements ApplicationContextAware {
	
	private static ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		if(SpringContext.ctx != null)
			return;
		
		SpringContext.ctx = ctx;
	}
	
	public static Object getBean(String name){
		return ctx.getBean(name);
	}
	
	public static <T> T getBean(Class<T> clazz){
		return ctx.getBean(clazz);
	}
	
	public static <T> T getBean(String beanName, Class<T> clazz){
		return ctx.getBean(beanName, clazz);
	}
	
	public static boolean containBean(String beanName){
		return ctx.containsBean(beanName);
	}
	
	public static <T> Collection<T> getBeanByType(Class<T> clazz){
		Map<String, T> beansMap = ctx.getBeansOfType(clazz);
		if(beansMap != null && !beansMap.isEmpty()){
			return beansMap.values();
		} else {
			return null;
		}
	}
	
	public static Resource getResource(String path){
		return ctx.getResource(path);
	}
	
	public static Resource[] getResources(String path) throws IOException {
		return ctx.getResources(path);
	}

}
