package com.hnup.common.webapi.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * The type Web api vo.
 *
 * @author XieXiongXiong
 * @date 2021 -06-18
 */
@Data
public class WebApiVO implements Serializable {


	private static final long serialVersionUID = -7186810976714599270L;
	/**
	 * Id
	 */
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
	/**
	 * sqlStr
	 */

	private String  sqlStr;

	private Integer  handleType;

	private byte[]   classBytes;

	private String   accessUrl;

	private String serviceType;

	private List<CustomFieldVO> customResponse;

	private List<CustomFieldVO> requestArgs;

	private String requestArgsStr;

	private String customResponseStr;

	private String method;

	private String argsType;

	private String methodBody;

	private String responseClass;
    /**返回类型  */
	private String returnType;
}
