package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.annotation.QField;
import io.github.chenfd99.jpaqueryobject.annotation.QGroup;
import io.github.chenfd99.jpaqueryobject.base.QType;
import io.github.chenfd99.jpaqueryobject.base.QueryObject;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.entity.User_;
import lombok.*;

import java.time.LocalDateTime;

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
public class UserGroupQO extends QueryObject<User> {


    @QGroup
    @QField(name = User_.NAME, value = QType.EQUAL)
    private String username;


    @QGroup
    @QField(value = QType.EQUAL, name = User_.EMAIL)
    private String email;

    @QGroup
    @QField(value = QType.GREATER_THAN_OR_EQUAL, name = User_.CREATED_TIME)
    private LocalDateTime beginTime;


    @QGroup("11sss")
    @QField(name = User_.NAME, value = QType.EQUAL)
    private String username1;


    @QGroup("11sss")
    @QField(value = QType.EQUAL, name = User_.EMAIL)
    private String email1;

}
