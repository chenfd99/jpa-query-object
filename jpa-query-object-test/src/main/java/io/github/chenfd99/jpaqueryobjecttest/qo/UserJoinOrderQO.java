package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.annotation.QField;
import io.github.chenfd99.jpaqueryobject.base.QueryObject;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.entity.User_;
import lombok.*;

import jakarta.persistence.criteria.JoinType;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinOrderQO extends QueryObject<User> {

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
