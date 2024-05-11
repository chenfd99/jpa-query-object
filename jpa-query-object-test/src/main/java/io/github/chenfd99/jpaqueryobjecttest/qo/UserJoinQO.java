package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.annotation.QField;
import io.github.chenfd99.jpaqueryobject.annotation.QFields;
import io.github.chenfd99.jpaqueryobject.base.QType;
import io.github.chenfd99.jpaqueryobject.base.QueryObject;
import io.github.chenfd99.jpaqueryobjecttest.entity.Purse_;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.entity.User_;
import lombok.*;

import javax.persistence.criteria.JoinType;
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
     * 查询的时候强制join ORDERS 和 PURSE 两个表
     * 此字段其他仅作为声明强制join的作用
     */
    @QFields({@QField(joinName = User_.ORDERS, joinType = JoinType.LEFT, forceJoin = true),
            @QField(joinName = User_.PURSE, joinType = JoinType.LEFT, forceJoin = true)})
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Long joinKeyword;


    /**
     * 根据PURSE的用户id查询
     */
    @QField(joinName = User_.PURSE, joinType = JoinType.LEFT, name = Purse_.USER, value = QType.EQUAL)
    private Long purseUserId;


    /**
     * 查询钱包余额大于等于此值的用户
     */
    @QField(joinName = User_.PURSE, joinType = JoinType.LEFT, name = Purse_.USER, value = QType.GREATER_THAN_OR_EQUAL)
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
    public boolean distinct() {
        return true;
    }
}
