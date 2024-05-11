package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.annotation.QField;
import io.github.chenfd99.jpaqueryobject.base.QType;
import io.github.chenfd99.jpaqueryobject.base.QueryObject;
import io.github.chenfd99.jpaqueryobjecttest.entity.Purse_;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.entity.User_;
import lombok.*;

import javax.persistence.criteria.JoinType;


/**
 * 用户查询条件
 *
 * @author ChenFD
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinQO extends QueryObject<User> {

    @QField(
            joinName = User_.PURSE,
            joinType = JoinType.LEFT,
            name = Purse_.USER,
            value = QType.EQUAL,
            forceJoin = true
    )
    private Long userId;


    @QField(
            joinName = User_.PURSE,
            joinType = JoinType.LEFT,
            name = Purse_.USER,
            value = QType.EQUAL
    )
    private Long userId2;

    @Override
    public boolean distinct() {
        return true;
    }
}
