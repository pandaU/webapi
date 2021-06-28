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
public class WebApiVO {

	private Long id;

	private String appKey;

	private String beanName;

	private String apiPath;

	private String methodName;

	private String classPath;

	private Integer  status;

	private String  utime;

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
    /**定义的返回类型*/
	private String responseClass;
    /**返回类型  */
	private String returnType;
}
