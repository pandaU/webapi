package com.hnup.common.webapi.service;

import com.hnup.common.webapi.model.JavaBeanDTO;
import com.hnup.common.webapi.repository.dao.JavaBeanDao;
import com.hnup.common.webapi.repository.entity.JavaBeanEntity;
import com.hnup.common.webapi.util.ClassUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * The type Java bean service.
 *
 * @author XieXiongXiong
 * @date 2021 -06-25
 */
@Service
public class JavaBeanService {
	/**
	 * Java bean dao
	 */
	private final JavaBeanDao javaBeanDao;
    @Autowired
	public JavaBeanService(JavaBeanDao javaBeanDao) {
		this.javaBeanDao = javaBeanDao;
	}

	/**
	 * List list.
	 *
	 * @param beanName the bean name
	 * @return the list
	 * @author XieXiongXiong
	 * @date 2021 -06-25 14:09:09
	 */
	public List<JavaBeanDTO> list(String beanName,String key){
		List<JavaBeanEntity> list = javaBeanDao.list(beanName,key);
		List<JavaBeanDTO> beanDTOList =new ArrayList<>();
		list.forEach(x->{
			JavaBeanDTO dto = new JavaBeanDTO();
			BeanUtils.copyProperties(x,dto);
			dto.setFields(ClassUtil.jsonToArray(x.getFieldStr()));
			beanDTOList.add(dto);
		});
		return beanDTOList;
	}
}
