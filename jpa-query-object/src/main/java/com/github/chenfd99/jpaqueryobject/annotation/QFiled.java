package com.github.chenfd99.jpaqueryobject.annotation;



import com.github.chenfd99.jpaqueryobject.base.QType;

import java.lang.annotation.*;


/**
 * 参与查询的字段注解
 *
 * @author chenfd
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
@Repeatable(QFiled.QGroup.class)
public @interface QFiled {

    /**
     * 实体对应的字段名称
     * 默认为被注解的字段名称
     */
    String name() default "";


    /**
     * 查询所对应的类型
     * 默认为相等查询
     */
    QType value() default QType.EQUAL;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    @Documented
    @interface QGroup {
        QFiled[] value() default {};

        QGroup.Type type() default QGroup.Type.OR;

        enum Type {
            AND, OR
        }
    }

}


