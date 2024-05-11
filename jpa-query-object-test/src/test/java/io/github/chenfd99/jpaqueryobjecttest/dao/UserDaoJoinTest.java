package io.github.chenfd99.jpaqueryobjecttest.dao;

import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.qo.UserJoinQO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * @author ChenFD
 */
@DataJpaTest(properties = "spring.jpa.properties.hibernate.format_sql=true")
class UserDaoJoinTest {
    @Autowired
    private UserDao userDao;
    @Autowired
    private OrderDao orderDao;


    @Test
    void testJoinPurse() {
        User user = new User();

        UserJoinQO qo = new UserJoinQO();
        userDao.findAll(qo);
    }

}