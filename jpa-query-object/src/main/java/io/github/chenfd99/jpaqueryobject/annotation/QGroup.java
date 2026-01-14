package io.github.chenfd99.jpaqueryobject.annotation;


import java.lang.annotation.*;


/**
 * or 分组查询
 * 当需要多个字段进行 or 查询时，可以使用此注解
 *
 * @author chenfd
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface QGroup {


    /**
     * 分组名称 用于两个条件 or 查询
     */
    String value() default "";
}


