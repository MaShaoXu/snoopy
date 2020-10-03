package com.sam.hadoop.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
@Import(HbaseTableSelector.class)
public @interface EnableHbaseTable {

    String[] basePackages() default {};

}
