package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.annotation.QField;
import io.github.chenfd99.jpaqueryobject.annotation.QFields;
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
public class UserForceJoinQO extends QueryObject<User> {

    /**
     * 查询的时候强制join ORDERS 和 PURSE 两个表
     * 此字段其他仅作为声明强制join的作用
     */
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @QFields({@QField(joinName = User_.ORDERS, joinType = JoinType.INNER, forceJoin = true),
            @QField(joinName = User_.PURSE, joinType = JoinType.INNER, forceJoin = true)})
    private Long joinKeyword;


}
