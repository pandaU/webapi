package com.hnup.common.webapi;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * <p>
 * The interface Enable web api auto configratioin.
 *
 * @author XieXiongXiong
 * @date 2021 -06-18
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(WebApiAutoConfiguration.class)
public @interface EnableWebApiAutoConfigratioin {
}
