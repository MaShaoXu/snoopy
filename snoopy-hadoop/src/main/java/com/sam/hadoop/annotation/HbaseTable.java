package com.sam.hadoop.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HbaseTable {
    String namespace() default "";
    String tableName() default "";
}
