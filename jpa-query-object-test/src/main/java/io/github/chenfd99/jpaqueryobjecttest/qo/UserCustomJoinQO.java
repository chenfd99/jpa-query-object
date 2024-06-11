package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.annotation.QField;
import io.github.chenfd99.jpaqueryobject.base.QueryObject;
import io.github.chenfd99.jpaqueryobjecttest.entity.Purse_;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.entity.User_;
import jakarta.persistence.criteria.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义join
 *
 * @author ChenFD
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCustomJoinQO extends QueryObject<User> {


    /**
     * 用户id
     */
    @QField(joinName = User_.PURSE, joinType = JoinType.LEFT, forceJoin = true)
    private Long userId;

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


    @Override
    protected void customJoin(Root<User> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        root.join(User_.ORDERS, JoinType.LEFT);
        root.join(User_.PURSE, JoinType.LEFT);
    }
}
