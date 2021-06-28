package com.hnup.common.webapi.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * The type Java bean dto.
 *
 * @author XieXiongXiong
 * @date 2021 -06-25
 */
@Data
public class JavaBeanDTO implements Serializable {
	private static final long serialVersionUID = 4488941396856441132L;
	/**
	 * Id
	 */
	private Long id;

	/**
	 * Bean name
	 */
	private String beanName;

	/**
	 * App key
	 */
	private String appKey;


	/**
	 * Class bytes
	 */
	private byte[] classBytes;


	private List<CustomFieldVO> fields;
}
