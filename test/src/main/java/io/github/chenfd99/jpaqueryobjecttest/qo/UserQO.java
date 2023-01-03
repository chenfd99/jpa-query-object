package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.annotation.QFiled;
import io.github.chenfd99.jpaqueryobject.base.QType;
import io.github.chenfd99.jpaqueryobject.base.QueryObject;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author ChenFD
 */
@Setter
@Getter
public class UserQO extends QueryObject<User> {
    @QFiled(name = "name", value = QType.EQUAL)
    private String username;

    @QFiled(name = "createdTime", value = QType.LESS_THAN_OR_EQUAL)
    private LocalDateTime createdTimeLE;


    @QFiled(name = "name", value = QType.EQUAL)
    @QFiled(name = "email", value = QType.LIKE)
    private String keyword;
}
