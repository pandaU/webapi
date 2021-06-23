package com.hnup.common.webapi.model;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * The type Custom method vo.
 *
 * @author XieXiongXiong
 * @date 2021 -06-23
 */
@Data
public class CustomMethodVO {
	/**
	 * Method name
	 */
	private String  methodName;

	/**
	 * Return type
	 */
	private String  returnType;

	/**
	 * Args type
	 */
	private List<String>  argsType;

	/**
	 * Body
	 */
	private String  body;
}
