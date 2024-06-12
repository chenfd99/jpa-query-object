package io.github.chenfd99.jpaqueryobjecttest.dao;

import io.github.chenfd99.jpaqueryobjecttest.base.BaseJoinTest;
import io.github.chenfd99.jpaqueryobjecttest.entity.Order;
import io.github.chenfd99.jpaqueryobjecttest.entity.Order_;
import io.github.chenfd99.jpaqueryobjecttest.entity.Purse;
import io.github.chenfd99.jpaqueryobjecttest.qo.UserCustomJoinQO;
import io.github.chenfd99.jpaqueryobjecttest.qo.UserJoinOrderQO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

/**
 * @author ChenFD
 */
@DataJpaTest
@ExtendWith(OutputCaptureExtension.class)
class UserDaoCustomJoinTest implements BaseJoinTest {
    @Autowired
    private UserDao userDao;

    @Test
    @DisplayName("自定义join")
    void customJoin(CapturedOutput output) {
        UserCustomJoinQO qo = new UserCustomJoinQO();
        userDao.findAll(qo);


        String outString = output.getOut();
        assertInnerJoinTable(outString, Purse.class);
    }


    @Test
    @DisplayName("根据用户id查询")
    void testJoinWithPurseUserId() {
        UserCustomJoinQO qo = new UserCustomJoinQO();
        qo.setUserId(122L);
        userDao.findAll(qo);
    }

    @Test
    @DisplayName("join Order orderNo")
    void testUserJoinOrder(CapturedOutput output) {
        UserJoinOrderQO qo = new UserJoinOrderQO();
        qo.setOrderNo("1111");
        userDao.findAll(qo);


        assertQueryCondition(output.getOut(), Order.class, Order_.ORDER_NO);
    }
}