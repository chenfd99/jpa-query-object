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
    void testJoinQuery() {
        UserQO qo = new UserQO();
        qo.setOrderNo("222222");
        userDao.findAll(qo);
    }

    @Test
    void testJoinQueryOr() {
        UserQO qo = new UserQO();
        qo.setOrderNoOrUsername("222222");
        userDao.findAll(qo);
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