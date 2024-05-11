package io.github.chenfd99.jpaqueryobjecttest.dao;

import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.qo.UserJoinQO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

/**
 * @author ChenFD
 */
@DataJpaTest(properties = "spring.jpa.properties.hibernate.format_sql=true")
class UserDaoJoinTest {
    @Autowired
    private UserDao userDao;

    @Test
    @DisplayName("无查询条件")
    void testJoin() {
        User user = new User();

        UserJoinQO qo = new UserJoinQO();
        userDao.findAll(qo);
    }


    @Test
    @DisplayName("根据钱包用户id查询")
    void testJoinWithPurseUserId() {
        User user = new User();

        UserJoinQO qo = new UserJoinQO();
        qo.setPurseUserId(122L);
        userDao.findAll(qo);
    }


    @Test
    @DisplayName("其他条件")
    void testJoinWithOther() {
        User user = new User();

        UserJoinQO qo = new UserJoinQO();
        qo.setOrderNo("1111");
        qo.setPurseBalanceGE(BigDecimal.valueOf(100.0));
        userDao.findAll(qo);
    }

}