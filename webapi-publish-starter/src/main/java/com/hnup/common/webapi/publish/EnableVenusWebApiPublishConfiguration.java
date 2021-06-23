package com.hnup.common.webapi.publish;

import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.lang.annotation.*;

/**
 * <p>
 * The interface Enable venus web api publish configuration.
 *
 * @author XieXiongXiong
 * @date 2021 -06-18
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(WebApiPublishAutoConfiguration.class)
@EnableTransactionManagement
public @interface EnableVenusWebApiPublishConfiguration {
}
