package com.hnup.common.webapi.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * The type Register entity.
 *
 * @author XieXiongXiong
 * @date 2021 -06-23
 */
@TableName("web_bean")
@Data
@Accessors(chain = true)
public class JavaBeanEntity extends Model<JavaBeanEntity> {
	/**
	 * Id
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * Bean name
	 */
	@TableField(value = "bean_name")
	private String beanName;

	/**
	 * App key
	 */
	@TableField(value = "app_key")
	private String appKey;


	@TableField(value = "class_bytes")
	private byte[] classBytes;
}
