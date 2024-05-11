package io.github.chenfd99.jpaqueryobject.annotation;

import java.lang.annotation.*;

/**
 * QFiled分组条件
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface QFields {
    QField[] value();

    Type type() default Type.OR;

    enum Type {
        AND, OR
    }
}