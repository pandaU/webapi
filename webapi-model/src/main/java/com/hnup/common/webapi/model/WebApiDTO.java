package com.hnup.common.webapi.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * The type Web api dto.
 *
 * @author XieXiongXiong
 * @date 2021 -06-25
 */
@Data
public class WebApiDTO implements Serializable {

	private static final long serialVersionUID = 4885468064720988013L;

	private Long id;

	private String returnType;

	private List<CustomFieldVO> customResponse;

	private List<CustomFieldVO> requestArgs;

	/**
	 * Method
	 */
	private String method;

	/**
	 * Response class
	 */
	private String responseClass;

	/**
	 * Sql str
	 */
	private String  sqlStr;

	/**
	 * Access url
	 */
	private String   accessUrl;

}
