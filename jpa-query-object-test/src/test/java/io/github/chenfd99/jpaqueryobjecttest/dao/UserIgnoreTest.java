package io.github.chenfd99.jpaqueryobjecttest.dao;

import io.github.chenfd99.jpaqueryobjecttest.qo.UserNotIgnoreEmptyQO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

/**
 * @author ChenFD
 */
@DataJpaTest
class UserIgnoreTest {
    @Autowired
    private UserDao userDao;


    @Test
    void testNotIgnoreEmpty() {
        var search = UserNotIgnoreEmptyQO.builder().username("").build();
        userDao.findAll(search);
    }

    @Test
    void testNotIgnoreBlank() {
        var search = UserNotIgnoreEmptyQO.builder().username(" ").build();
        userDao.findAll(search);
    }

}