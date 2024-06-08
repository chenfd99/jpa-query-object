package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.annotation.QField;
import io.github.chenfd99.jpaqueryobject.base.QueryObject;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.entity.User_;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.criteria.*;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinOrderQO extends QueryObject<User> {

    @Getter
    private CriteriaQuery<?> cq;

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
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        this.cq = cq;
        return super.toPredicate(root, cq, cb);
    }
}
