package com.hnup.common.webapi.publish.model;

import lombok.Data;

@Data
public class WebApi {
	private Long id;

	/**
	 * App key
	 */
	private String appKey;

	/**
	 * Bean name
	 */
	private String beanName;

	/**
	 * Api path
	 */
	private String apiPath;

	/**
	 * Method name
	 */
	private String methodName;

	/**
	 * Class path
	 */
	private String classPath;

	/**
	 * Status
	 */
	private Integer  status;

	/**
	 * Utime
	 */
	private String  utime;
}
