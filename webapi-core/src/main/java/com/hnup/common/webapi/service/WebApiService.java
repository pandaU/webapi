package com.hnup.common.webapi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hnup.common.lang.exception.DeclareException;
import com.hnup.common.webapi.model.WebApiVO;
import com.hnup.common.webapi.repository.dao.WebApiDao;
import com.hnup.common.webapi.repository.entity.RegisterEntity;
import com.hnup.common.webapi.repository.entity.WebApiEntity;
import com.hnup.common.webapi.repository.mapper.RegisterMapper;
import com.hnup.common.webapi.util.ClassUtil;
import com.hnup.common.webapi.util.RegisterBean;
import com.hnup.common.webapi.util.WebApiClassLoader;
import javassist.ClassPool;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		String str = readStr(request.getInputStream());
		ObjectMapper objectMapper = new ObjectMapper();
		Map<Object,Object> map = new HashMap<>();
		if (!StringUtils.isEmpty(str)){
			map = objectMapper.readValue(str, HashMap.class);
		}
		String uri = request.getRequestURI();
		uri= regUrl(uri);
		List<WebApiVO> list = list(null, null, uri , key);
		if (CollectionUtils.isEmpty(list)) {
			throw new DeclareException("接口不存在");
		}
		WebApiVO vo = list.get(0);
		String sqlStr = vo.getSqlStr();
		Integer handleType = vo.getHandleType();
		for (Map.Entry entry : map.entrySet()) {
			if (StringUtils.isEmpty(entry.getValue().toString())){
				continue;
			}
			if (validateSql(entry.getValue().toString())){
				throw new DeclareException("参数不合法");
			}
			sqlStr = sqlStr.replace("#{" + entry.getKey().toString() + "}", "'"+entry.getValue().toString()+"'");
		}
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

	private String readStr(ServletInputStream input){
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
}
