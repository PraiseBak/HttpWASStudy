package com.otc.otc.annotations;

import com.otc.otc.dto.MethodEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {
    String value() default "";
    String defaultValue() default "";

    boolean required() default true;
}
