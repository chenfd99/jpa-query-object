package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.annotation.QField;
import io.github.chenfd99.jpaqueryobject.base.QType;
import io.github.chenfd99.jpaqueryobject.base.QueryObject;
import io.github.chenfd99.jpaqueryobjecttest.entity.Purse_;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.entity.User_;
import jakarta.persistence.criteria.JoinType;
import lombok.*;

import java.math.BigDecimal;


/**
 * 此类默认join ORDERS和PURSE 两个表进行查询数据
 *
 * @author ChenFD
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinQO extends QueryObject<User> {


    /**
     * 根据PURSE的用户id查询
     */
    @QField(joinName = User_.PURSE,
            joinType = JoinType.LEFT,
            name = Purse_.USER,
            value = QType.EQUAL)
    private User purseUserId;


    /**
     * 查询钱包余额大于等于此值的用户
     */
    @QField(joinName = User_.PURSE,
            name = Purse_.BALANCE,
            value = QType.GREATER_THAN_OR_EQUAL)
    private BigDecimal purseBalanceGE;


    /**
     * 根据订单号查询
     */
    @QField(joinName = User_.ORDERS, joinType = JoinType.LEFT)
    private String orderNo;


    /**
     * 默认去重
     */
    @Override
    protected Boolean distinct() {
        return true;
    }
}
