package com.hnup.common.webapi.model;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * The type Register vo.
 *
 * @author XieXiongXiong
 * @date 2021 -06-23
 */
@Data
public class RegisterVO {

	/**
	 * Bean name
	 */
	private  String beanName;

	/**
	 * Fields
	 */
	private  List<CustomFieldVO> fields;

	/**
	 * Methods
	 */
	private  List<CustomMethodVO> methods;



}
