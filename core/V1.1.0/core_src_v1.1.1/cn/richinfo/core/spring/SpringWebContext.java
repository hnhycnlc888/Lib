package cn.richinfo.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * spring mvc 上下文
 */
public class SpringWebContext implements ApplicationContextAware {
	
	private static ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		if(SpringWebContext.ctx != null)
			return;
		
		SpringWebContext.ctx = ctx;
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

}
