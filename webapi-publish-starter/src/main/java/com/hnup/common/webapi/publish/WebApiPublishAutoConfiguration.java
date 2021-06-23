package com.hnup.common.webapi.publish;

import com.hnup.common.webapi.EnableWebApiAutoConfigratioin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * The type Web api publish auto configuration.
 *
 * @author XieXiongXiong
 * @date 2021 -06-18
 */
@Configuration
@ComponentScan(basePackages = {
	"com.hnup.common.webapi.publish"
})
@EnableWebApiAutoConfigratioin
@ConditionalOnBean(annotation = EnableVenusWebApiPublishConfiguration.class)
public class WebApiPublishAutoConfiguration {
}
