package com.hnup.common.webapi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnup.common.lang.exception.DeclareException;
import com.hnup.common.webapi.model.CustomFieldVO;
import com.hnup.common.webapi.model.RegisterVO;
import com.hnup.common.webapi.model.WebApiVO;
import com.hnup.common.webapi.repository.dao.WebApiDao;
import com.hnup.common.webapi.repository.entity.RegisterEntity;
import com.hnup.common.webapi.repository.entity.WebApiEntity;
import com.hnup.common.webapi.repository.mapper.RegisterMapper;
import com.hnup.common.webapi.util.ApplicationContextRegister;
import com.hnup.common.webapi.util.ClassUtil;
import com.hnup.common.webapi.util.RegisterBean;
import com.hnup.common.webapi.util.WebApiClassLoader;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * <p>
 * The type Web api service.
 *
 * @author XieXiongXiong
 * @date 2021 -06-15
 */
@Service
public class WebApiService implements ApplicationContextAware {

	@Value("${venus.app_key:webapi}")
	private String key;

	private final WebApiDao webApiDao;

	private ApplicationContext applicationContext;

    @Autowired(required = false)
	private RegisterMapper registerMapper;


	private static Pattern pattern = Pattern.compile("\\b(and|exec|insert|select|drop|grant|alter|delete|update|count|chr|mid|master|truncate|char|declare|or)\\b|(\\*|;|\\+|'|%)");

	@Autowired(required = false)
	public WebApiService(WebApiDao webApiDao) {
		this.webApiDao = webApiDao;
	}

	public WebApiVO sqlApi(String registerApi,String sqlString ,String apiPath,WebApiVO webApiVo) throws Exception {
		String DTOClassName = null;
		if (!CollectionUtils.isEmpty(webApiVo.getCustomResponse())){
			String DTOName = "DTO" +UUID.randomUUID().toString().replace("-","");
			Map<String, Object> javaBean = ClassUtil.generateJavaBean(DTOName, webApiVo.getCustomResponse(), null);
			DTOClassName = javaBean.get("beanName").toString();
			RegisterEntity entity = new RegisterEntity();
			entity.setBeanName(javaBean.get("beanName").toString());
			entity.setAppKey(key);
			entity.setClassBytes((byte[]) javaBean.get("bytes"));
			entity.insert();
		}
		Map<String, Object> map = ClassUtil.generateSQLClass("", null, null, "com.hnup.common.webapi.service.WebApiService", null,webApiVo.getMethod());
		Class<?> cl = (Class<?>)map.get("class");
		Object bean = RegisterBean.registerBean(cl.getSimpleName(), cl, ApplicationContextRegister.getApplicationContext());
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
		info.setMethod(webApiVo.getMethod());
		info.setResponseClass(DTOClassName);
		ObjectMapper mapper = new ObjectMapper();
		String args =Optional.ofNullable(webApiVo.getRequestArgs()).map(x->{
			try {
				return mapper.writeValueAsString(webApiVo.getRequestArgs());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return null;
		}).orElse(null);
		String resp =Optional.ofNullable(webApiVo.getCustomResponse()).map(x->{
			try {
				return mapper.writeValueAsString(webApiVo.getCustomResponse());
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return null;
		}).orElse(null);
		info.setRequestArgsStr(args);
		info.setCustomResponseStr(resp);
		saveWebApi(info);
		return info;
	}
	public ResponseEntity<?> javaBean(String beanName, RegisterVO registerVO) throws NotFoundException, CannotCompileException, IOException {
		Map<String, Object> map = ClassUtil.generateJavaBean(beanName, registerVO.getFields(), registerVO.getMethods());
		RegisterEntity entity = new RegisterEntity();
		entity.setBeanName(map.get("beanName").toString());
		entity.setAppKey(key);
		entity.setClassBytes((byte[]) map.get("bytes"));
		entity.insert();
		return ResponseEntity.ok(entity);
	}
	public int saveWebApi(WebApiVO webApiVo) {
		WebApiEntity entity = new WebApiEntity();
		BeanUtils.copyProperties(webApiVo, entity);
		return webApiDao.saveWebApi(entity);
	}

	public WebApiVO selectById(Long id) {
		WebApiEntity entity = webApiDao.selectById(id);
		WebApiVO vo = new WebApiVO();
		BeanUtils.copyProperties(entity, vo);
		return vo;
	}

	public int upById(WebApiVO webApiVo) {
		WebApiEntity entity = new WebApiEntity();
		BeanUtils.copyProperties(webApiVo, entity);
		return webApiDao.upById(entity);
	}

	public List<WebApiVO> list(String beanName, String methodName, String apiMapping, String key) {
		List<WebApiEntity> list = webApiDao.list(beanName, methodName, apiMapping , key);
		List<WebApiVO> apiVos = list.stream().map(entity -> {
			WebApiVO vo = new WebApiVO();
			BeanUtils.copyProperties(entity, vo);
			return vo;
		}).collect(Collectors.toList());
		return apiVos;
	}

	public List<WebApiVO> listBySql(String sqlStr) {
		List<WebApiEntity> list = webApiDao.listBySql(sqlStr);
		List<WebApiVO> apiVos = list.stream().map(entity -> {
			WebApiVO vo = new WebApiVO();
			BeanUtils.copyProperties(entity, vo);
			return vo;
		}).collect(Collectors.toList());
		return apiVos;
	}

	public int updateBySql(String sqlStr) {
		return webApiDao.updateBySql(sqlStr);
	}

	public int insertBySql(String sqlStr) {
		return webApiDao.insertBySql(sqlStr);
	}

	public List<Map<String,Object>> doService(HttpServletRequest request) throws IOException {
		String uri = request.getRequestURI();
		uri= regUrl(uri);
		List<WebApiVO> list = list(null, null, uri , key);
		if (CollectionUtils.isEmpty(list)) {
			throw new DeclareException("接口不存在");
		}
		WebApiVO vo = list.get(0);
		String sqlStr = vo.getSqlStr();
		Integer handleType = vo.getHandleType();
		String argsStr = vo.getRequestArgsStr();
		sqlStr =  getSql(argsStr,request,sqlStr);
		List<Map<String,Object>> resp = new ArrayList<>();
		switch (handleType) {
			case 1:
				insertBySql(sqlStr);
				break;
			case 2:
				updateBySql(sqlStr);
				break;
			case 3:
				resp = listBySqlReturnMap(sqlStr);
				break;
			default:
				throw new DeclareException("未找到执行的sql");
		}
		return resp;
	}

	public List<Map<String,Object>> listBySqlReturnMap(String sqlStr) {
		return webApiDao.listBySqlReturnMap(sqlStr);
	}

	/**
	 * 是否含有sql注入，返回true表示含有
	 *
	 * @param sqlStr
	 * @return
	 */
	public Boolean validateSql(String sqlStr) {
		Matcher matcher = pattern.matcher(sqlStr.toLowerCase());
		return matcher.find();
	}

	public String regUrl(String url){
		return url.replaceAll("/+", "/");
	}

	@PostConstruct
	public void initApiAndBean(){
		ClassPool pool = ClassUtil.pool;
		WebApiClassLoader loader = WebApiClassLoader.loader;

		QueryWrapper<RegisterEntity> wrapper = new QueryWrapper<>();
		wrapper.eq("app_key",key).select("bean_name as beanName,class_bytes as classBytes");
		List<RegisterEntity> list1 = registerMapper.selectList(wrapper);
		list1.forEach(x->{
			String beanName = x.getBeanName();
			byte[] bytes = x.getClassBytes();
			loader.defineClass(beanName, bytes);
			try {
				pool.makeClass(new ByteArrayInputStream(bytes));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		List<WebApiVO> list = list(null, null, null, key);
		list.forEach(x->{
			String apiPath = x.getApiPath();
			String name = x.getBeanName();
			byte[] bytes = x.getClassBytes();
			Class<?> aClass = loader.defineClass(name, bytes);
			String[] split = name.split("\\.");
			String beanName = split[split.length-1];
			ApplicationContext context = this.applicationContext;
			Object bean = RegisterBean.registerBean(beanName, aClass,context);
			aClass = bean.getClass();
			try {
				RegisterBean.controlCenter(aClass, context,2,null,apiPath);
				pool.makeClass(new ByteArrayInputStream(bytes));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
             this.applicationContext = applicationContext;
	}

	private  String readStr(ServletInputStream input){
		String resp = null;
		ByteArrayOutputStream outputStream = null;
		try {
			outputStream = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			int len = 0;
			while ((len = input.read(bytes)) != -1){
				outputStream.write(bytes);
			}
			byte[] array = outputStream.toByteArray();
			resp = new String(array, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream.close();
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return resp;
	}
	private static Boolean isNumber(String key,List<CustomFieldVO> list){
		CustomFieldVO fieldVO = new CustomFieldVO();
		fieldVO.setFieldName(key);
		int index = -1;
		String defaultValue = "java.lang.String";
		Boolean isNumber = false;
		if((index = list.indexOf(fieldVO)) > -1){
			CustomFieldVO vo1 = list.get(index);
			isNumber = !vo1.getFieldType().equals(defaultValue);
		}
		return isNumber;
	}

	private  String getSql(String argsStr,HttpServletRequest request,String sqlStr) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Map<Object,Object> map = new HashMap<>(16);
		List<CustomFieldVO> args = Optional.ofNullable(argsStr).map(x->{
			List<CustomFieldVO> fieldVOArrayList = new ArrayList<>();
			try {
				List value = objectMapper.readValue(x, List.class);
				value.forEach(y->{
					Map<String,String> stringMap = (Map<String,String>)y;
					CustomFieldVO  fieldVO =  new CustomFieldVO();
					fieldVO.setColumn(stringMap.get("column"));
					fieldVO.setFieldName(stringMap.get("fieldName"));
					fieldVO.setFieldType(stringMap.get("fieldType"));
					fieldVOArrayList.add(fieldVO);
				});
				return fieldVOArrayList;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}).orElse(null);
		String str = readStr(request.getInputStream());
		if (!StringUtils.isEmpty(str)){
			map = objectMapper.readValue(str, HashMap.class);
		}else {
			Map<String, String[]> stringMap = request.getParameterMap();
			for (Map.Entry<String, String[]> e: stringMap.entrySet()) {
				map.put(e.getKey(),e.getValue().length > 1 ? Arrays.asList(e.getValue()) : e.getValue()[0] );
			}
		}

		for (Map.Entry entry : map.entrySet()) {
			if (StringUtils.isEmpty(entry.getValue().toString())){
				continue;
			}
			if (validateSql(entry.getValue().toString())){
				throw new DeclareException("参数不合法");
			}
			List<String> StringList = new ArrayList<>();
			if (entry.getValue() instanceof  List) {
				if (!StringUtils.isEmpty(str)){
					List<Object> arr = (List<Object>) entry.getValue();
					StringList = arr.stream().map(x -> {
						return x.toString();
					}).collect(Collectors.toList());
				}else {
					StringList = (List<String>)entry.getValue();
				}
				map.put(entry.getKey(),String.join("','",StringList));
				if (isNumber(entry.getKey().toString(),args)){
					map.put(entry.getKey(),String.join(",",StringList));
				}
			}
			if (isNumber(entry.getKey().toString(),args)){
				sqlStr = sqlStr.replace("#{" + entry.getKey().toString() + "}", entry.getValue().toString());
			}else {
				sqlStr = sqlStr.replace("#{" + entry.getKey().toString() + "}", "'"+entry.getValue().toString()+"'");
			}
		}
		return sqlStr;
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
