package io.github.chenfd99.jpaqueryobject.annotation;


import io.github.chenfd99.jpaqueryobject.base.QType;

import javax.persistence.criteria.JoinType;
import java.lang.annotation.*;


/**
 * 参与查询的字段注解
 *
 * @author chenfd
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
@Repeatable(QGroup.class)
public @interface QFiled {

    /**
     * 数据库实体对应的字段名称
     * 默认为被注解的字段名称
     */
    String name() default "";


    /**
     * 查询所对应的类型
     * 默认为相等查询
     */
    QType value() default QType.EQUAL;


    /**
     * 连表查询对应的关系字段名称
     */
    String joinName() default "";

    /**
     * 连表查询类型
     */
    JoinType joinType() default JoinType.INNER;

}


