package com.jdragon.tljrobot.tljutils.zFeign;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Jdragon
 * @email: 1061917196@qq.com
 * @Date: 2020.11.22 23:20
 * @Description:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ZFeign {

    String baseUrl();

    String basePath() default "";

    /**
     *  解构提取返回结果
     **/
    String depth() default "";

    String[] headers() default {};

    Class<?> fallback() default ZFeignFallbackImpl.class;

    String successCode() default "20000";

    String successField() default "code";

    String messageField() default "message";
}
