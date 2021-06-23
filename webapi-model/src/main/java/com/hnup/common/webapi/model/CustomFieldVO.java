package com.hnup.common.webapi.model;

import lombok.Data;

/**
 * <p>
 * The type Custom field vo.
 *
 * @author XieXiongXiong
 * @date 2021 -06-23
 */
@Data
public class CustomFieldVO {
	/**
	 * Name
	 */
	private String fieldName;

	/**
	 * Type
	 */
	private String fieldType;
}
