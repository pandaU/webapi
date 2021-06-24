package com.hnup.common.webapi.model;

import lombok.Data;

import java.util.Objects;

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

	/**
	 * Column
	 */
	private String column;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CustomFieldVO fieldVO = (CustomFieldVO) o;
		return Objects.equals(fieldName, fieldVO.fieldName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fieldName);
	}
}
