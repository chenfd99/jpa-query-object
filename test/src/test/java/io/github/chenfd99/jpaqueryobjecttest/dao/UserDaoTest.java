package io.github.chenfd99.jpaqueryobjecttest.dao;

import io.github.chenfd99.jpaqueryobjecttest.qo.UserQO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

/**
 * @author ChenFD
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest(properties = "spring.jpa.properties.hibernate.format_sql=true")
class UserDaoTest {
    @Autowired
    private UserDao userDao;


    @Test
    void testQuery() {
        UserQO qo = new UserQO();
        qo.setUsername("chen");
        qo.setKeyword("chen11");
        qo.setCreatedTimeLE(LocalDateTime.now());
        userDao.findAll(qo);
    }


    @Test
    void testNoRestriction() {
        userDao.findAll(new UserQO());
    }

}