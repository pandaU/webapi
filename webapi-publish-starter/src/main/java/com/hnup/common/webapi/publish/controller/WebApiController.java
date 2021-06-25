package com.hnup.common.webapi.publish.controller;


import com.hnup.common.lang.exception.DeclareException;
import com.hnup.common.webapi.model.JavaBeanDTO;
import com.hnup.common.webapi.model.RegisterVO;
import com.hnup.common.webapi.model.WebApiVO;
import com.hnup.common.webapi.publish.response.ResponseFactory;
import com.hnup.common.webapi.service.JavaBeanService;
import com.hnup.common.webapi.service.WebApiService;
import com.hnup.common.webapi.util.ApplicationContextRegister;
import com.hnup.common.webapi.util.ClassUtil;
import com.hnup.common.webapi.util.RegisterBean;
import com.hnup.common.webapi.util.WebApiClassLoader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * <p>
 * The type Test controller.
 *
 * @author XieXiongXiong
 * @date 2021 -06-15
 */
@RestController
@SuppressWarnings("ALL")
@Api(tags = "动态部署接口说明")
public class WebApiController {
    private static Pattern pattern = Pattern.compile("\\b(exec|drop|grant|alter|delete|truncate|create)\\b");

	ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	final WebApiService webApiService;

    @Value("${venus.app_key:webapi}")
    private String key;
	@Value("${venus.default.package:com.hnup.common.webapi}")
	private  String defaultPackage;

	@Autowired
	public WebApiController(WebApiService webApiService) {
		this.webApiService = webApiService;
	}

	@Autowired
	private JavaBeanService javaBeanService;

	@PutMapping("sqlApi")
	@ApiOperation(value = "发布sql类型api")
	public ResponseEntity<?> registerSqlApi(@RequestBody WebApiVO webApiVo) {
		webApiVo.setMethod(Optional.ofNullable(webApiVo.getMethod()).map(x->{
			return webApiVo.getMethod().toLowerCase();
		}).orElse("get"));
		String sqlString = webApiVo.getSqlStr();
		String apiPath = webApiVo.getApiPath();
		if (StringUtils.isEmpty(sqlString) || StringUtils.isEmpty(apiPath) || CollectionUtils.isEmpty(webApiVo.getRequestArgs())){
			throw new DeclareException("参数不合法");
		}
		if (validateSql(sqlString)){
			throw new DeclareException("当前sql不合法");
		}
		apiPath = webApiService.regUrl(apiPath);
		if (validateUrl(apiPath)){
			throw new DeclareException("不支持@PathVariable");
		}
		String registerApi = apiPath;
		final List iocMappings = ApplicationContextRegister.getIOCMappings();
		if (apiPath.startsWith("/")){
			apiPath = ApplicationContextRegister.getEnablePrefix(defaultPackage)  + apiPath;
		}else {
			apiPath = ApplicationContextRegister.getEnablePrefix(defaultPackage) + "/" + apiPath;
		}
		List<WebApiVO> webApiVos = webApiService.list(null, null, apiPath, key);
		final List<String> apiPaths = webApiVos.stream().map(WebApiVO::getAccessUrl).collect(Collectors.toList());
		if (iocMappings.contains(apiPath) || apiPaths.contains(apiPath)){
			throw  new DeclareException("当前apiPath被占用");
		}
		try {
			return ResponseFactory.builder().api(webApiService.sqlApi(registerApi,sqlString,apiPath,webApiVo)).build().get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new DeclareException("注册接口失败");
	}

	@PutMapping("javaBean")
	@ApiOperation(value = "javaBean注册")
	public ResponseEntity<?> registerBean(@RequestBody RegisterVO registerVO) {
		String beanName = registerVO.getBeanName();
		WebApiClassLoader loader = WebApiClassLoader.loader;
		Class<?> aClass1 = null;
		try {
			aClass1 = loader.loadClass(defaultPackage + "." + beanName);
		} catch (Exception e) {
		}
		if (aClass1 != null){
			throw new DeclareException(beanName + "类已存在");
		}
		try {
			return webApiService.javaBean(beanName,registerVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new DeclareException("注册bean失败");
	}

	@PutMapping("commonApi")
	@ApiOperation(value = "发布普通类型api")
	public ResponseEntity<?> registerApi(@RequestBody WebApiVO webApiVo) {
		String apiPath = webApiVo.getApiPath();
		if (StringUtils.isEmpty(apiPath)){
			throw new DeclareException("参数不合法");
		}
		apiPath = webApiService.regUrl(apiPath);
		if (validateUrl(apiPath)){
			throw new DeclareException("不支持@PathVariable");
		}
		String registerApi = apiPath;
		final List iocMappings = ApplicationContextRegister.getIOCMappings();
		if (apiPath.startsWith("/")){
			apiPath = ApplicationContextRegister.getEnablePrefix(defaultPackage)  + apiPath;
		}else {
			apiPath = ApplicationContextRegister.getEnablePrefix(defaultPackage) + "/" + apiPath;
		}
		List<WebApiVO> webApiVos = webApiService.list(null, null, apiPath, key);
		final List<String> apiPaths = webApiVos.stream().map(WebApiVO::getApiPath).collect(Collectors.toList());
		if (iocMappings.contains(apiPath) || apiPaths.contains(apiPath)){
			throw  new DeclareException("当前apiPath被占用");
		}
		try {
			Map<String, Object> map = ClassUtil.generateApiClass("", null, null, webApiVo.getServiceType(), null, webApiVo.getMethodBody(), webApiVo.getArgsType());
			Class<?> cl = (Class<?>)map.get("class");
			Object bean = RegisterBean.registerBean(cl.getSimpleName(), cl,ApplicationContextRegister.getApplicationContext());
			Class<?> aClass = bean.getClass();
			RegisterBean.controlCenter(aClass, ApplicationContextRegister.getApplicationContext(), 2, null, registerApi);
			WebApiVO info = new WebApiVO();
			info.setBeanName(map.get("beanName").toString());
			info.setApiPath(registerApi);
			info.setMethodName("action");
			info.setClassPath(map.get("path").toString());
			info.setStatus(1);
			info.setAppKey(key);
			info.setClassBytes((byte[]) map.get("bytes"));
			info.setAccessUrl(apiPath);
			webApiService.saveWebApi(info);
			return ResponseFactory.builder().api(info).build().get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new DeclareException("注册接口失败,请检查参数");
	}
    @GetMapping("api")
	@ApiOperation(value = "api列表")
	public ResponseEntity<?> api(@RequestParam(required = false) String accessUrl,@RequestParam(required = false) String method, @RequestParam(required = false)String responseClass) {
		List<WebApiVO> list = webApiService.list(responseClass, method, accessUrl, key);
		return ResponseFactory.builder().apiList(list).build().getList();
	}

	@GetMapping("javaBean")
	@ApiOperation(value = "javaBean列表")
	public ResponseEntity<?> javaBean(@RequestParam(required = false)String beanName) {
		List<JavaBeanDTO> list = javaBeanService.list(beanName, key);
		return ResponseEntity.ok(list);
	}
	/**
	 * 是否含有sql注入，返回true表示含有
	 * @param sqlStr
	 * @return
	 */
	private Boolean validateSql(String sqlStr){
		return pattern.matcher(sqlStr).find();
	}

	private Boolean validateUrl(String apiPath){
		return apiPath.contains("{") || apiPath.contains("}");
	}
}