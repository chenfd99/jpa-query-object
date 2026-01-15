package io.github.chenfd99.jpaqueryobjecttest.qo;

import io.github.chenfd99.jpaqueryobject.annotation.QField;
import io.github.chenfd99.jpaqueryobject.base.QType;
import io.github.chenfd99.jpaqueryobject.base.QueryObject;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.entity.User_;
import lombok.*;

/**
 * 不忽略空白字符查询条件
 *
 * @author ChenFD
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotIgnoreEmptyQO extends QueryObject<User> {


    @Override
    protected boolean isIgnoreEmptyString(String str) {
        return false;
    }

    /**
     * 用户名
     */
    @QField(name = User_.NAME, value = QType.EQUAL)
    private String username;


}
