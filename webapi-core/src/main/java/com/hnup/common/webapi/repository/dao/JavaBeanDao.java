package com.hnup.common.webapi.repository.dao;

import com.hnup.common.webapi.repository.entity.JavaBeanEntity;

import java.util.List;

/**
 * <p>
 * The interface Java bean dao.
 *
 * @author XieXiongXiong
 * @date 2021 -06-25
 */
public interface JavaBeanDao {
	/**
	 * List list.
	 *
	 * @param beanName the bean name
	 * @param key the key
	 * @return the list
	 * @author XieXiongXiong
	 * @date 2021 -06-25 11:46:06
	 */
	List<JavaBeanEntity> list(String beanName,String key);
}
