package com.hnup.common.webapi.publish.controller;


import com.hnup.common.lang.exception.DeclareException;
import com.hnup.common.webapi.config.PathConfig;
import com.hnup.common.webapi.model.RegisterVO;
import com.hnup.common.webapi.model.WebApiVO;
import com.hnup.common.webapi.publish.response.ResponseFactory;
import com.hnup.common.webapi.repository.entity.RegisterEntity;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

	/**
	 * Register api string.
	 *
	 * @param file       the file
	 * @param methodName the method name
	 * @param apiMapping the api mapping
	 * @return the string
	 * @throws Exception the exception
	 * @author XieXiongXiong
	 * @date 2021 -06-15 08:42:25
	 */
	//@PostMapping("/registerApi")
	@ApiOperation(value = "文件类型动态部署")
	public Object registerApi(@RequestParam("file") MultipartFile file, String methodName, String apiMapping) throws Exception {
		String fileName = file.getOriginalFilename();
		if (file.isEmpty() || !fileName.endsWith(PathConfig.JAVA_SUFFIX)) {
			return "请选择java文件";
		}
		if (!StringUtils.hasLength(fileName)) {
			return "文件名称不合法";
		}
		String filePath = PathConfig.EXT_JAVA_DIR;
		File dest = new File(filePath + fileName);
		try {
			file.transferTo(dest);
			String[] strings = fileName.split("\\.");
			String apiName = strings[0];
			String javaPath = apiName + ".class";
			String name = WebApiClassLoader.getName(filePath + fileName);
			WebApiClassLoader loader = WebApiClassLoader.loader;
			/**动态编译*/
			Boolean compilerResp = ClassUtil.compiler(dest.getAbsolutePath());
			if (!compilerResp) {
				return "代码编译失败，请检查代码书写格式";
			}
			Class<?> aClass = loader.loadClass(name);
			final char[] chars = apiName.toCharArray();
			chars[0] = chars[0] < 91 ? (char) (chars[0] + 32) : chars[0];
			String apiNameDown = new String(chars);
			Object bean = RegisterBean.registerBean(apiNameDown, aClass,ApplicationContextRegister.getApplicationContext());
			Class<?> aClass1 = bean.getClass();
			final RestController annotation = aClass1.getAnnotation(RestController.class);
			if (annotation == null) {
				return "发布失败,请确保类上有@RestController";
			}
			RegisterBean.controlCenter(aClass1, ApplicationContextRegister.getApplicationContext(), 2, methodName, apiMapping);
			//// TODO: 2021/6/11  将发布信息存储到mysql 便于后期维护管理
			List<WebApiVO> list = webApiService.list(apiNameDown, methodName, null,key);
			WebApiVO info = new WebApiVO();
			info.setBeanName(apiNameDown);
			info.setApiPath(apiMapping);
			info.setMethodName(methodName);
			info.setClassPath(filePath + javaPath);
			info.setStatus(1);
			if (list.size() > 0) {
				final Long id = list.get(0).getId();
				info.setId(id);
				info.setUtime(threadLocal.get().format(new Date()));
				if (!apiMapping.equals(list.get(0).getApiPath())) {
					RegisterBean.controlCenter(aClass1, ApplicationContextRegister.getApplicationContext(), 3, methodName, list.get(0).getApiPath());
				}
				webApiService.upById(info);
			} else {
				webApiService.saveWebApi(info);
			}
			return "发布成功";
		} catch (IOException e) {
		}
		return "发布失败,请确保方法上有@RequestMapping";
	}

	@PostMapping("registerSqlApi")
	@ApiOperation(value = "sql类型动态部署")
	public ResponseEntity<?> registerSqlApi(@RequestBody WebApiVO webApiVo) {
		String sqlString = webApiVo.getSqlStr();
		String apiPath = webApiVo.getApiPath();
		if (StringUtils.isEmpty(sqlString) || StringUtils.isEmpty(apiPath)){
			throw new DeclareException("参数不合法");
		}
		if (validateSql(sqlString)){
			throw new DeclareException("当前sql不合法");
		}
		apiPath = webApiService.regUrl(apiPath);
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
			Map<String, Object> map = ClassUtil.generateSQLClass("", null, null, "com.hnup.common.webapi.service.WebApiService", null);
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
			info.setHandleType(getSqlType(sqlString));
			info.setSqlStr(sqlString);
			info.setClassBytes((byte[]) map.get("bytes"));
			info.setAccessUrl(apiPath);
			webApiService.saveWebApi(info);
			return ResponseFactory.builder().api(info).build().get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new DeclareException("注册接口失败");
	}

	@PostMapping("registerBean")
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
			Map<String, Object> map = ClassUtil.generateJavaBean(beanName, registerVO.getFields(), registerVO.getMethods());
			Class<?> cl = (Class<?>)map.get("class");
			RegisterEntity entity = new RegisterEntity();
			entity.setBeanName(map.get("beanName").toString());
			entity.setAppKey(key);
			entity.setClassBytes((byte[]) map.get("bytes"));
			entity.insert();
			return ResponseEntity.ok(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new DeclareException("注册bean失败");
	}

	@PostMapping("registerApi")
	@ApiOperation(value = "普通类型api动态部署")
	public ResponseEntity<?> registerApi(@RequestBody WebApiVO webApiVo) {
		String apiPath = webApiVo.getApiPath();
		if (StringUtils.isEmpty(apiPath)){
			throw new DeclareException("参数不合法");
		}
		apiPath = webApiService.regUrl(apiPath);
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
	/**
	 * 是否含有sql注入，返回true表示含有
	 * @param sqlStr
	 * @return
	 */
    private Boolean validateSql(String sqlStr){
		return pattern.matcher(sqlStr).find();
	}

	/**
	 * 是否含有sql注入，返回true表示含有
	 * @param sqlStr
	 * @return
	 */
	private Integer getSqlType(String sqlStr){
		if (sqlStr.contains("select")){
			return 3;
		}
		if (sqlStr.contains("update")){
			return 2;
		}
		if (sqlStr.contains("insert")){
			return 1;
		}
		return 0;
	}
}