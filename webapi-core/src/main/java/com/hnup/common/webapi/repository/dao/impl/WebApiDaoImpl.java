package com.hnup.common.webapi.repository.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnup.common.webapi.repository.dao.WebApiDao;
import com.hnup.common.webapi.repository.entity.WebApiEntity;
import com.hnup.common.webapi.repository.mapper.WebApiMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * The type Web api dao.
 *
 * @author XieXiongXiong
 * @date 2021 -06-18
 */
@Repository
public class WebApiDaoImpl implements WebApiDao {

	/**
	 * Web api mapper
	 */
	private final WebApiMapper webApiMapper;

	/**
	 * Web api dao
	 *
	 * @param webApiMapper web api mapper
	 */
	@Autowired(required = false)
	public WebApiDaoImpl(WebApiMapper webApiMapper) {
		this.webApiMapper = webApiMapper;
	}

	/**
	 * Save web api int.
	 *
	 * @param WebApiEntity the web api entity
	 * @return the int
	 * @author XieXiongXiong
	 * @date 2021 -06-18 10:25:54
	 */
	@Override
	public int saveWebApi(WebApiEntity WebApiEntity) {
		return webApiMapper.insert(WebApiEntity);
	}

	/**
	 * Select by id web api entity.
	 *
	 * @param id the id
	 * @return the web api entity
	 * @author XieXiongXiong
	 * @date 2021 -06-18 10:25:54
	 */
	@Override
	public WebApiEntity selectById(Long id) {
		return webApiMapper.selectById(id);
	}

	/**
	 * Up by id int.
	 *
	 * @param WebApiEntity the web api entity
	 * @return the int
	 * @author XieXiongXiong
	 * @date 2021 -06-18 10:25:54
	 */
	@Override
	public int upById(WebApiEntity WebApiEntity) {
		return webApiMapper.updateById(WebApiEntity);
	}

	/**
	 * List list.
	 *
	 * @param responseClass   the bean name
	 * @param method the method name
	 * @param apiMapping the api mapping
	 * @return the list
	 * @author XieXiongXiong
	 * @date 2021 -06-18 10:25:54
	 */
	@Override
	public List<WebApiEntity> list(String responseClass, String method, String apiMapping, String key) {
		QueryWrapper<WebApiEntity> qw = new QueryWrapper<>();
		if (responseClass != null && !responseClass.isEmpty()) {
			qw.eq("response_class", responseClass);
		}
		if (method != null && !method.isEmpty()) {
			qw.eq("method", method);
		}
		if (apiMapping != null && !apiMapping.isEmpty()) {
			qw.eq("access_url", apiMapping);
		}
		if (key != null && !key.isEmpty()) {
			qw.eq("app_key", key);
		}
		qw.select("return_type,response_class,method,request_args_str,custom_response_str,access_url,class_bytes,id,bean_name,method_name,api_path,status,class_path,date_format(utime,'%Y-%m-%d %H:%i:%s') as utime,app_key,handle_type,sql_str");

		return webApiMapper.selectList(qw);
	}

	@Override
	public List<WebApiEntity> listBySql(String sqlStr) {
		return webApiMapper.selectBySql(sqlStr);
	}

	@Override
	public int updateBySql(String sqlStr) {
		return webApiMapper.updateBySql(sqlStr);
	}

	@Override
	public int insertBySql(String sqlStr) {
		return webApiMapper.updateBySql(sqlStr);
	}

	@Override
	public List<Map<String, Object>> listBySqlReturnMap(String sqlStr) {
		return webApiMapper.listBySqlReturnMap(sqlStr);
	}
}
