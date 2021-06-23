package com.hnup.common.webapi.repository.dao;

import com.hnup.common.webapi.repository.entity.WebApiEntity;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * The interface Web api dao.
 *
 * @author XieXiongXiong
 * @date 2021 -06-18
 */
public interface WebApiDao {

	/**
	 * save web api entity.
	 *
	 * @param WebApiEntity the WebApiEntity
	 * @return the web api entity
	 * @author XieXiongXiong
	 * @date 2021 -06-18 10:25:54
	 */
	public int saveWebApi(WebApiEntity WebApiEntity);

	/**
	 * Select by id web api entity.
	 *
	 * @param id the id
	 * @return the web api entity
	 * @author XieXiongXiong
	 * @date 2021 -06-18 10:25:54
	 */
	public WebApiEntity selectById(Long id);

	/**
	 * Up by id int.
	 *
	 * @param WebApiEntity the web api entity
	 * @return the int
	 * @author XieXiongXiong
	 * @date 2021 -06-18 10:25:54
	 */
	public int upById(WebApiEntity WebApiEntity);

	/**
	 * List list.
	 *
	 * @param beanName   the bean name
	 * @param methodName the method name
	 * @param apiMapping the api mapping
	 * @return the list
	 * @author XieXiongXiong
	 * @date 2021 -06-18 10:25:54
	 */
	public List<WebApiEntity> list(String beanName, String methodName, String apiMapping, String key);

	public List<WebApiEntity> listBySql(String sqlStr);

	public int updateBySql(String sqlStr);

	public int insertBySql(String sqlStr);

	public List<Map<String,Object>> listBySqlReturnMap(String sqlStr);
}
