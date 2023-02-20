package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.annotation.QFiled;
import io.github.chenfd99.jpaqueryobject.base.QType;
import io.github.chenfd99.jpaqueryobject.base.QueryObject;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户查询条件
 *
 * @author ChenFD
 */
@Setter
@Getter
public class UserQO extends QueryObject<User> {
    /**
     * 用户名
     */
    @QFiled(name = "name", value = QType.EQUAL)
    private String username;

    /**
     * 创建时间小于
     */
    @QFiled(name = "createdTime", value = QType.LESS_THAN_OR_EQUAL)
    private LocalDateTime createdTimeLE;


    /**
     * 用户名或者邮箱
     */
    @QFiled(name = "name", value = QType.EQUAL)
    @QFiled(name = "email", value = QType.LIKE)
    private String keyword;


    /**
     * 订单号
     */
    @QFiled(joinName = "orders")
    private String orderNo;


    /**
     * 订单号或者用户名称
     */
    @QFiled(joinName = "orders", name = "orderNo")
    @QFiled(name = "name")
    private String orderNoOrUsername;

}
