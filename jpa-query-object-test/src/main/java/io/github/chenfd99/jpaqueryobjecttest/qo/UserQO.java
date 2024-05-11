package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.annotation.QFiled;
import io.github.chenfd99.jpaqueryobject.annotation.QGroup;
import io.github.chenfd99.jpaqueryobject.base.QType;
import io.github.chenfd99.jpaqueryobject.base.QueryObject;
import io.github.chenfd99.jpaqueryobjecttest.entity.Order_;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.entity.User_;
import lombok.*;

import javax.persistence.criteria.JoinType;
import java.time.LocalDateTime;
import java.util.Collection;

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
public class UserQO extends QueryObject<User> {

    @QFiled(value = QType.IN, name = User_.ID)
    private Collection<Long> idIn;

    @QFiled(value = QType.NOT_IN, name = User_.ID)
    private Collection<Long> idNotIn;


    /**
     * 用户名
     */
    @QFiled(name = User_.NAME, value = QType.EQUAL)
    private String username;

    /**
     * 用户名模糊查询
     */
    @QFiled(name = User_.NAME, value = QType.LIKE)
    private String usernameLike;

    /**
     * 创建时间小于
     */
    @QFiled(name = User_.CREATED_TIME, value = QType.LESS_THAN_OR_EQUAL)
    private LocalDateTime createdTimeLE;


    /**
     * 用户名或者邮箱
     */
    @QGroup({@QFiled(name = User_.NAME, value = QType.EQUAL),
            @QFiled(name = User_.EMAIL, value = QType.LIKE)})
    private String nameEqualOrEmailLike;

    @QFiled(name = User_.NAME, value = QType.NOT_EQUAL)
    private String nameNotEqual;


    @QFiled(name = User_.NAME, value = QType.IS_NULL)
    private Boolean nameIsNull;

    @QFiled(name = User_.NAME, value = QType.NOT_NULL)
    private Boolean nameNotNull;

    @QFiled(name = User_.NAME, value = QType.EQUAL_OR_NULL)
    private String nameEqualOrNull;

    @QFiled(name = User_.NAME, value = QType.NOT_EQUAL_OR_NULL)
    private String nameNotEqualOrNull;

    /**
     * 用户名和邮箱
     */
    @QGroup(value = {
            @QFiled(name = User_.NAME, value = QType.EQUAL),
            @QFiled(name = User_.EMAIL, value = QType.EQUAL)
    }, type = QGroup.Type.AND)
    private String nameEqualAndEmailEqual;


    /**
     * 订单号
     */
    @QFiled(joinName = User_.ORDERS)
    private String orderNo;


    /**
     * 订单号或者用户名称
     */
    @QGroup({@QFiled(joinName = User_.ORDERS, name = Order_.ORDER_NO, joinType = JoinType.LEFT),
            @QFiled(name = User_.NAME)})
    private String orderNoOrUsername;


    /**
     * 使用 group and 条件
     */
    @QGroup(value = {@QFiled(name = User_.NAME), @QFiled(name = User_.EMAIL)}, type = QGroup.Type.AND)
    private String groupAnd;

}
