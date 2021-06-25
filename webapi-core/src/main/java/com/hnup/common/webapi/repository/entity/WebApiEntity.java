package com.hnup.common.webapi.repository.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * The type Web api info.
 *
 * @author XieXiongXiong
 * @date 2021 -06-15
 */
@TableName("web_api_info")
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class WebApiEntity extends Model<WebApiEntity> {
	@TableId(value = "id",type = IdType.ASSIGN_ID)
	private Long id;

	@TableField(value = "app_key")
	private String appKey;

	@TableField(value = "bean_name")
	private String beanName;

	@TableField(value = "api_path")
	private String apiPath;

	@TableField(value = "method_name")
	private String methodName;

	@TableField(value = "class_path")
	private String classPath;

	@TableField(value = "status")
	private Integer  status;

	@TableField(value = "utime")
	private String  utime;

	@TableField(value = "sql_str")
	private String sqlStr;

	@TableField(value = "handle_type")
	private Integer  handleType;

	@TableField(value = "class_bytes")
	private byte[]   classBytes;

	@TableField(value = "access_url")
	private String   accessUrl;

	@TableField(value = "method")
	private String method;

	@TableField(value = "request_args_str")
	private String requestArgsStr;

	@TableField(value = "custom_response_str")
	private String customResponseStr;

	@TableField(value = "response_class")
	private String responseClass;

	@TableField(value = "return_type")
	private String returnType;
}
