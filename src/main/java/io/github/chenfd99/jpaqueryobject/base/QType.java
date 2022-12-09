package io.github.chenfd99.jpaqueryobject.base;

/**
 * 查询类型
 */
public enum QType {
    IS_NULL,
    NOT_NULL,

    /**
     * 相等
     */
    EQUAL,

    /**
     * 相等或者null
     */
    EQUAL_OR_NULL,
    /**
     * 不相等
     */
    NOT_EQUAL,
    /**
     * 不相等或者null
     */
    NOT_EQUAL_OR_NULL,
    /**
     * 大于等于(Number)
     */
    GE,
    /**
     * 大于(Number)
     */
    GT,
    /**
     * 小于等于(Number)
     */
    LE,
    /**
     * 小于(Number)
     */
    LT,
    /**
     * 模糊查询 ('%xx%)
     */
    LIKE,
    /**
     * 模糊查询 ('%xx)
     */
    LIKE_START,
    /**
     * 模糊查询 ('xx%')
     */
    LIKE_END,
    /**
     * not like
     */
    NOT_LIKE,
    /**
     * in 查询
     * 如: id in (1,2,3,44)
     */
    IN,

    NOT_IN,

    /**
     * 大于(Comparable)
     */
    GREATER_THAN,
    /**
     * 大于等于(Comparable)
     */
    GREATER_THAN_OR_EQUAL,
    /**
     * 小于(Comparable)
     */
    LESS_THAN,
    /**
     * 小于等于(Comparable)
     */
    LESS_THAN_OR_EQUAL,
}
