package io.github.chenfd99.jpaqueryobjecttest.dao;

import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.qo.UserGroupQO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author ChenFD
 */
@DataJpaTest
class UserDaoGroupTest {
    @Autowired
    private UserDao userDao;


    @Test
    @DisplayName("多个字段组合成一个or查询")
    void testGroup() {
        System.out.println("准备的数据");

        String email = secure().nextAlphabetic(6);
        String name = secure().nextAlphabetic(6);

        List<User> userList = List.of(User.builder().name(name).build(), User.builder().email(email).build());
        userList = userDao.saveAllAndFlush(userList);

        UserGroupQO search = UserGroupQO.builder()
                //下面是一个 or 条件
                .username(name)
                .email(email)
                .emailOrName("111")
                .beginTime(LocalDateTime.now().minusDays(1))
                //下面是一个 or 条件
                .username1(name)
                .email1(email)
                .build();

        List<User> users = userDao.findAll(search);
        System.out.println("查找后的数据");
        for (User user : users) {
            System.out.println(user);
        }

        assertEquals(userList.size(), users.size());
    }

    /**
     * QFields 和 QField 混合使用
     * 组合成一个条件为
     * u1_0.email=?  QField条件
     * and (u1_0.name=? or u1_0.email=?) QFields条件
     */
    @Test
    @DisplayName("QFields 和 QField 组合")
    void testGroup2() {
        UserGroupQO search = UserGroupQO.builder().emailOrName("email").build();
        userDao.findAll(search);
    }


    @Test
    @DisplayName("QGroup 只有一个")
    void testGroup3() {
        UserGroupQO search = UserGroupQO.builder().email("email").build();
        userDao.findAll(search);
    }

}