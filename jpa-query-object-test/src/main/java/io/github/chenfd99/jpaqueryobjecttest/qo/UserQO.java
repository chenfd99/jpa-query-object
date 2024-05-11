package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.annotation.QField;
import io.github.chenfd99.jpaqueryobject.annotation.QFields;
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

    @QField(value = QType.IN, name = User_.ID)
    private Collection<Long> idIn;

    @QField(value = QType.NOT_IN, name = User_.ID)
    private Collection<Long> idNotIn;


    /**
     * 用户名
     */
    @QField(name = User_.NAME, value = QType.EQUAL)
    private String username;

    /**
     * 用户名模糊查询
     */
    @QField(name = User_.NAME, value = QType.LIKE)
    private String usernameLike;

    /**
     * 创建时间小于
     */
    @QField(name = User_.CREATED_TIME, value = QType.LESS_THAN_OR_EQUAL)
    private LocalDateTime createdTimeLE;


    /**
     * 用户名或者邮箱
     */
    @QFields({@QField(name = User_.NAME, value = QType.EQUAL),
            @QField(name = User_.EMAIL, value = QType.LIKE)})
    private String nameEqualOrEmailLike;

    @QField(name = User_.NAME, value = QType.NOT_EQUAL)
    private String nameNotEqual;


    @QField(name = User_.NAME, value = QType.IS_NULL)
    private Boolean nameIsNull;

    @QField(name = User_.NAME, value = QType.NOT_NULL)
    private Boolean nameNotNull;

    @QField(name = User_.NAME, value = QType.EQUAL_OR_NULL)
    private String nameEqualOrNull;

    @QField(name = User_.NAME, value = QType.NOT_EQUAL_OR_NULL)
    private String nameNotEqualOrNull;

    /**
     * 用户名和邮箱
     */
    @QFields(
            value = {
                    @QField(name = User_.NAME, value = QType.EQUAL),
                    @QField(name = User_.EMAIL, value = QType.EQUAL)
            },
            type = QFields.Type.AND)
    private String nameEqualAndEmailEqual;


    /**
     * 订单号
     */
    @QField(joinName = User_.ORDERS, joinType = JoinType.LEFT, name = Order_.ORDER_NO)
    private String orderNo;


    /**
     * 订单号或者用户名称
     */
    @QFields({@QField(joinName = User_.ORDERS, joinType = JoinType.LEFT, name = Order_.ORDER_NO),
            @QField(name = User_.NAME)})
    private String orderNoOrUsername;


    /**
     * 使用 group and 条件
     */
    @QFields(value = {@QField(name = User_.NAME), @QField(name = User_.EMAIL)},
            type = QFields.Type.AND)
    private String groupAnd;

}
