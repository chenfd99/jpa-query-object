package com.github.chenfd99.jpaqueryobject.annotation;


import java.lang.annotation.*;

/**
 * 排序注解
 *
 * @author chenfd
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Documented
@Repeatable(QOrderBy.QOrderGroup.class)
public @interface QOrderBy {
    /**
     * 排序的字段名称
     */
    String value() default "";


    /**
     * 同一个字段下多个OrderBy的顺序
     * <p>
     * 默认按order升序,都没有设置order值,那么按QOrderBy声明顺序排序
     * <p>
     * <p>
     * <strong>例1: </strong> 按order排序
     * </p>
     * <pre><code class='java'>
     *  &#64;QOrderBy("email")
     *  &#64;QOrderBy("name")
     *  &#64;QOrderBy(value = "id", order = 1)
     *  private QOrder orderGroup;
     *  //id: DESC,email: DESC,name: DESC
     * </code></pre>
     * <p>
     * <strong>例2: </strong> 按QOrderBy声明顺序排序
     * </p>
     * <pre><code class='java'>
     *  &#64;QOrderBy("email")
     *  &#64;QOrderBy("name")
     *  &#64;QOrderBy("id")
     *  private QOrder orderGroup;
     *  //email: DESC,name: DESC,id: DESC
     * </code></pre>
     * <p>
     * <p>
     * <strong>例3: </strong> 按order排序
     * </p>
     * <pre><code class='java'>
     *  &#64;QOrderBy(value = "email", order = 2)
     *  &#64;QOrderBy(value = "name", order = 3)
     *  &#64;QOrderBy(value = "id", order = 1)
     *  private QOrder orderGroup;
     *  //id: DESC,email: DESC,name: DESC
     * </code></pre>
     */
    int order() default 100000;


    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    @Documented
    @interface QOrderGroup {

        QOrderBy[] value() default {};
    }

}
