package com.hnup.common.webapi.util;

import com.hnup.common.core.starter.prefix.VenusCoreModuleUrlPrefix;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

@Component
public class ApplicationContextRegister implements ApplicationContextAware {
    private static ApplicationContext APPLICATION_CONTEXT;
    /**
     * 设置spring上下文
     * @param applicationContext spring上下文
     * @throws BeansException
     * */
    @Override  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        APPLICATION_CONTEXT = applicationContext;
    }

    /**
     * 获取容器
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return APPLICATION_CONTEXT;
    }

    /**
     * 获取容器对象
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> type) {
        return APPLICATION_CONTEXT.getBean(type);
    }

    public static <T> T getBean(String name,Class<T> clazz){
        return APPLICATION_CONTEXT.getBean(name, clazz);
    }

    public static Object getBean(String name){
        return APPLICATION_CONTEXT.getBean(name);
    }

	public static List<String> getIOCMappings(){
		List<String> urls =new ArrayList();
		RequestMappingHandlerMapping mapping = ApplicationContextRegister
			.getBean(RequestMappingHandlerMapping.class);
		// 获取url与类和方法的对应信息
		Map<RequestMappingInfo, HandlerMethod> map = mapping
			.getHandlerMethods();
		for (RequestMappingInfo info : map.keySet()) {
			// 获取url的Set集合，一个方法可能对应多个url
			Set<String> patterns = info.getPatternsCondition().getPatterns();
			for (String url : patterns) {
				//把结果存入静态变量中程序运行一次次方法之后就不用再次请求次方法
				urls.add(url);
			}
		}
		return urls;
	}

	public static String getEnablePrefix(String packageName){
    	String str = "";
		Map<String, Object> beansWithAnnotation = ApplicationContextRegister.getApplicationContext().getBeansWithAnnotation(VenusCoreModuleUrlPrefix.class);
		Iterator<Map.Entry<String, Object>> var1 = beansWithAnnotation.entrySet().iterator();
		while (var1.hasNext()){
			Class<?> aClass =var1.next().getValue().getClass();
			VenusCoreModuleUrlPrefix annotation = (VenusCoreModuleUrlPrefix) AnnotationUtils.findAnnotation(aClass, VenusCoreModuleUrlPrefix.class);
			if (annotation != null){
				str = StringUtils.isNotBlank(annotation.scanBasePackage()) &&  annotation.scanBasePackage().equals(packageName) ? annotation.value() : "";
			}
		}
		return str;
	}
}
