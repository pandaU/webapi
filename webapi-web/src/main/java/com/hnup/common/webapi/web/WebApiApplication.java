package com.hnup.common.webapi.web;

import com.hnup.common.core.starter.EnableVenusCoreConfiguration;
import com.hnup.common.core.starter.prefix.EnableVenusCoreModuleUrlPrefix;
import com.hnup.common.webapi.publish.EnableVenusWebApiPublishConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <p>
 * The type Web api application.
 *
 * @author XieXiongXiong
 * @date 2021 -06-18
 */

@EnableVenusWebApiPublishConfiguration
@EnableVenusCoreConfiguration
@EnableVenusCoreModuleUrlPrefix
@EnableTransactionManagement
@SpringBootApplication
public class WebApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(WebApiApplication.class,args);
	}
}
