package io.github.chenfd99.jpaqueryobjecttest.dao;

import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.qo.UserCustomJoinQO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * @author ChenFD
 */
@DataJpaTest(properties = "spring.jpa.properties.hibernate.format_sql=true")
class UserDaoCustomJoinTest {
    @Autowired
    private UserDao userDao;

    @Test
    @DisplayName("无查询条件")
    void testJoin() {
        User user = new User();

        UserCustomJoinQO qo = new UserCustomJoinQO();
        userDao.findAll(qo);
    }


    @Test
    @DisplayName("根据用户id查询")
    void testJoinWithPurseUserId() {
        User user = new User();

        UserCustomJoinQO qo = new UserCustomJoinQO();
        qo.setUserId(122L);
        userDao.findAll(qo);
    }

    @Test
    @DisplayName("其他条件")
    void testJoinWithOther() {
        User user = new User();

        UserCustomJoinQO qo = new UserCustomJoinQO();
        qo.setOrderNo("1111");
        qo.setUserId(222L);
        userDao.findAll(qo);
    }
}