package com.hnup.common.webapi.web;

import com.hnup.common.webapi.util.ApplicationContextRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class TestController {
	@RequestMapping("demo")
	public Object test(HttpServletRequest request){
		List urls =new ArrayList();
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
}
