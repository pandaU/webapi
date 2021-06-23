package com.hnup.common.webapi;

import com.hnup.common.core.starter.prefix.VenusCoreModuleUrlPrefix;
import com.hnup.common.webapi.config.PathConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * <p>
 * The type Web api auto configuration.
 *
 * @author XieXiongXiong
 * @date 2021 -06-18
 */
@Configuration
@ComponentScan(basePackages = {
	"com.hnup.common.webapi"
})
@MapperScan(basePackages = {"com.hnup.common.webapi.repository.mapper", "com.baomidou.springboot.mapper*"})
@VenusCoreModuleUrlPrefix(scanBasePackage = "com.hnup.common.webapi", value = PathConfig.URL_PREFIX)
public class WebApiAutoConfiguration {

}
