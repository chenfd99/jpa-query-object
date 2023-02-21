package io.github.chenfd99.jpaqueryobject.annotation;

import java.lang.annotation.*;

/**
 * QFiled分组条件
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface QGroup {
    QFiled[] value() default {};

    QGroup.Type type() default QGroup.Type.OR;

    enum Type {
        AND, OR
    }
}