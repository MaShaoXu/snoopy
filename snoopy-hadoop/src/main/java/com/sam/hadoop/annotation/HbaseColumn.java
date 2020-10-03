package com.sam.hadoop.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HbaseColumn {
    String family() default "";
    String qualifier() default "";
}
