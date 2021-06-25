package com.hnup.common.webapi.repository.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnup.common.webapi.repository.dao.JavaBeanDao;
import com.hnup.common.webapi.repository.entity.JavaBeanEntity;
import com.hnup.common.webapi.repository.mapper.JavaBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * The type Java bean dao.
 *
 * @author XieXiongXiong
 * @date 2021 -06-25
 */
@Repository
public class JavaBeanDaoImpl implements JavaBeanDao {
	/**
	 * Mapper
	 */
	private final JavaBeanMapper mapper;
    @Autowired
	public JavaBeanDaoImpl(JavaBeanMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public List<JavaBeanEntity> list(String beanName,String key) {
		QueryWrapper<JavaBeanEntity> wrapper = new QueryWrapper<>();
		if (!StringUtils.isEmpty(beanName)){
			wrapper.like("bean_name",beanName);
		}
		if (!StringUtils.isEmpty(key)){
			wrapper.like("app_key",key);
		}
		return mapper.selectList(wrapper);
	}
}
