package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.base.QueryObject;
import io.github.chenfd99.jpaqueryobjecttest.entity.Order_;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.entity.User_;
import lombok.*;

import javax.persistence.criteria.*;
import java.util.List;


@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCustomJoinQO2 extends QueryObject<User> {

    /**
     * 根据订单号查询
     */
    private String orderNo2;


    @Override
    protected List<Predicate> customPredicate(Root<User> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        if (orderNo2 == null || orderNo2.isEmpty()) {
            return super.customPredicate(root, cq, cb);
        }

        Join<User, ?> join = createJoin(root, User_.ORDERS);
        Predicate predicate = cb.equal(join.get(Order_.ORDER_NO), orderNo2);
        return List.of(predicate);
    }
}
