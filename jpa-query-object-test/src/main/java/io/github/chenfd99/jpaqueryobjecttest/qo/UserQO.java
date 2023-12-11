package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.annotation.QFiled;
import io.github.chenfd99.jpaqueryobject.annotation.QGroup;
import io.github.chenfd99.jpaqueryobject.base.QType;
import io.github.chenfd99.jpaqueryobject.base.QueryObject;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
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

    @QFiled(value = QType.IN, name = "id")
    private Collection<Long> idIn;

    @QFiled(value = QType.NOT_IN, name = "id")
    private Collection<Long> idNotIn;


    /**
     * 用户名
     */
    @QFiled(name = "name", value = QType.EQUAL)
    private String username;

    /**
     * 用户名模糊查询
     */
    @QFiled(name = "name", value = QType.LIKE)
    private String usernameLike;

    /**
     * 创建时间小于
     */
    @QFiled(name = "createdTime", value = QType.LESS_THAN_OR_EQUAL)
    private LocalDateTime createdTimeLE;


    /**
     * 用户名或者邮箱
     */
    @QFiled(name = "name", value = QType.EQUAL)
    @QFiled(name = "email", value = QType.LIKE)
    private String nameEqualOrEmailLike;

    @QFiled(name = "name", value = QType.NOT_EQUAL)
    private String nameNotEqual;


    @QFiled(name = "name", value = QType.IS_NULL)
    private Boolean nameIsNull;

    @QFiled(name = "name", value = QType.NOT_NULL)
    private Boolean nameNotNull;

    @QFiled(name = "name", value = QType.EQUAL_OR_NULL)
    private String nameEqualOrNull;

    @QFiled(name = "name", value = QType.NOT_EQUAL_OR_NULL)
    private String nameNotEqualOrNull;

    /**
     * 用户名和邮箱
     */
    @QGroup(
            value = {
                    @QFiled(name = "name", value = QType.EQUAL),
                    @QFiled(name = "email", value = QType.EQUAL)
            },
            type = QGroup.Type.AND
    )
    private String nameEqualAndEmailEqual;


    /**
     * 订单号
     */
    @QFiled(joinName = "orders")
    private String orderNo;


    /**
     * 订单号或者用户名称
     */
    @QFiled(joinName = "orders", name = "orderNo", joinType = JoinType.LEFT)
    @QFiled(name = "name")
    private String orderNoOrUsername;


    /**
     * 使用 group and 条件
     */
    @QGroup(
            value = {@QFiled(name = "name"), @QFiled(name = "email")},
            type = QGroup.Type.AND
    )
    private String groupAnd;

}
