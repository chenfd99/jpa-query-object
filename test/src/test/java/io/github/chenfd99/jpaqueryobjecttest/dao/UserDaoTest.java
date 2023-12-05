package io.github.chenfd99.jpaqueryobjecttest.dao;

import io.github.chenfd99.jpaqueryobjecttest.entity.Order;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.qo.UserQO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.util.List;
import java.util.Set;

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
        userDao.findAll(qo);
    }

    @Test
    void testLikeQuery() {
        UserQO qo = new UserQO();
        qo.setUsernameLike("chen");
        userDao.findAll(qo);
    }

    @Test
    void testGroupOr() {
        UserQO qo = new UserQO();
        qo.setKeyword("chen");
        userDao.findAll(qo);
    }


    @Test
    void testJoinQuery() {
        UserQO qo = new UserQO();
        qo.setOrderNo("20231212xxxxxxxx");
        userDao.findAll(qo);
    }

    @Test
    void testJoinQueryOr() {
        UserQO qo = new UserQO();
        qo.setOrderNoOrUsername("222222");
        userDao.findAll(qo);
    }


    @Test
    void testGroupAnd() {
        UserQO qo = new UserQO();
        qo.setGroupAnd("chen");
        userDao.findAll(qo);
    }


    @Test
    void testIn() {
        userDao.save(new User());
        userDao.save(new User());
        userDao.save(new User());

        userDao.findAll().forEach(System.out::println);
        System.out.println();

        UserQO qo = new UserQO();
        qo.setIdIn(Set.of(1L, 2L, 7898767L));
        List<User> users = userDao.findAll(qo);
        users.forEach(System.out::println);
        System.out.println("k");

        userDao.findAll().forEach(System.out::println);

    }

    @Test
    void testNoRestriction() {
        userDao.findAll(new UserQO());
    }


    @Test
    void testSpecification() {
        /*
           select
                  u1_0.id,
                  u1_0.created_time,
                  u1_0.email,
                  u1_0.name
              from
                  t_user u1_0
              join
                  t_order o1_0
                      on u1_0.id=o1_0.member_id
              where
                  o1_0.order_no=?
         */
        Specification<User> specification = (root, query, cb) -> {
            Join<User, Order> join = root.join("orders", JoinType.INNER);
            return cb.and(cb.equal(join.get("orderNo"), "20231112"));
        };

        userDao.findAll(specification);
    }
}