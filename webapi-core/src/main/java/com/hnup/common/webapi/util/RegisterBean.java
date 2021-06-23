package com.hnup.common.webapi.util;

import com.hnup.common.core.starter.prefix.EnableVenusCoreModuleUrlPrefix;
import com.hnup.common.core.starter.prefix.ModuleUrlPrefixRequestMappingHandlerMapping;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * <p>
 * The type Register bean.
 *
 * @author XieXiongXiong
 * @date 2021 -06-15
 */
public class RegisterBean {

    /**
     * Register bean object.
     *
     * @param name the name
     * @param cl   the cl
     * @return the object
     * @author XieXiongXiong
     * @date 2021 -06-15 09:12:33
     */
    public static Object registerBean(String name, Class cl ,ApplicationContext applicationContext) {
        //将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;

        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
		ClassLoader beanClassLoader = defaultListableBeanFactory.getBeanClassLoader();
		defaultListableBeanFactory.setBeanClassLoader(WebApiClassLoader.loader);
		// 通过BeanDefinitionBuilder创建bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(cl);
        try {
            defaultListableBeanFactory.registerBeanDefinition(name, beanDefinitionBuilder.getRawBeanDefinition());
        }catch (BeanDefinitionOverrideException e){
            System.out.println("bean 已经注册");
        }
		Object bean =  configurableApplicationContext.getBean(name);

        return bean;
    }

    /**
     * Control center.
     *
     * @param controllerClass the controller class
     * @param context         the context
     * @param type            the type
     * @param methodName      the method name
     * @param apiMapping      the api mapping
     * @throws IllegalAccessException the illegal access exception
     * @throws Exception              the exception
     * @author XieXiongXiong
     * @date 2021 -06-15 09:12:33
     */
    public static void controlCenter(Class<?> controllerClass, ApplicationContext context, Integer type, String methodName, String apiMapping) throws IllegalAccessException, Exception{
        //获取RequestMappingHandlerMapping
        RequestMappingHandlerMapping requestMappingHandlerMapping=(RequestMappingHandlerMapping) context.getBean(RequestMappingHandlerMapping.class);
		Method getMappingForMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class, "getMappingForMethod",Method.class,Class.class);

        //设置私有属性为可见
        getMappingForMethod.setAccessible(true);
        //获取类中的方法
        Method[] method_arr = controllerClass.getMethods();
        for (Method method : method_arr) {
            //判断方法上是否有注解RequestMapping
			RequestMapping annotation = method.getAnnotation(RequestMapping.class);
			if (annotation != null) {
                //获取到类的RequestMappingInfo
				RequestMappingInfo mappingInfo = (RequestMappingInfo) getMappingForMethod.invoke(requestMappingHandlerMapping, method,controllerClass);
				mappingInfo = mappingInfo.combine(RequestMappingInfo.paths(apiMapping).build());
                if(type == 1){
                    //注册
                    registerMapping(requestMappingHandlerMapping, mappingInfo, controllerClass, method ,context);
                }else if(type == 2){
                    //取消注册
                    unRegisterMapping(requestMappingHandlerMapping, mappingInfo);
                    registerMapping(requestMappingHandlerMapping, mappingInfo, controllerClass, method,context);
                }else if(type == 3){
                    unRegisterMapping(requestMappingHandlerMapping, mappingInfo);
                }

            }
        }
    }

    /**
     * registerMapping(注册mapping到spring容器中)
     *
     * @param requestMappingHandlerMapping the request mapping handler mapping
     * @param mappingInfo                  the mapping info
     * @param controllerClass              the controller class
     * @param method                       the method
     * @throws Exception              the exception
     * @throws IllegalAccessException the illegal access exception
     * @author XieXiongXiong
     * @date 2021 -06-15 09:12:33
     * @Exception 异常对象
     * @since CodingExample 　Ver(编码范例查看) 1.1
     */
    public static void registerMapping(RequestMappingHandlerMapping requestMappingHandlerMapping, RequestMappingInfo mappingInfo, Class<?> controllerClass, Method method ,ApplicationContext applicationContext) throws Exception, IllegalAccessException{
        requestMappingHandlerMapping.registerMapping(mappingInfo, applicationContext.getBean(controllerClass),method);
    }

    /**
     * unRegisterMapping(spring容器中删除mapping)
     *
     * @param requestMappingHandlerMapping the request mapping handler mapping
     * @param mappingInfo                  the mapping info
     * @throws Exception              the exception
     * @throws IllegalAccessException the illegal access exception
     * @author XieXiongXiong
     * @date 2021 -06-15 09:12:33
     * @Exception 异常对象
     * @since CodingExample 　Ver(编码范例查看) 1.1
     */
    public static void unRegisterMapping(RequestMappingHandlerMapping requestMappingHandlerMapping, RequestMappingInfo mappingInfo) throws Exception, IllegalAccessException{
        requestMappingHandlerMapping.unregisterMapping(mappingInfo);
    }
}
