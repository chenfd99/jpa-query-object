package io.github.chenfd99.jpaqueryobjecttest.dao;

import io.github.chenfd99.jpaqueryobjecttest.base.BaseJoinTest;
import io.github.chenfd99.jpaqueryobjecttest.entity.Order;
import io.github.chenfd99.jpaqueryobjecttest.entity.Order_;
import io.github.chenfd99.jpaqueryobjecttest.entity.Purse;
import io.github.chenfd99.jpaqueryobjecttest.qo.UserCustomJoinQO;
import io.github.chenfd99.jpaqueryobjecttest.qo.UserCustomJoinQO2;
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
    @DisplayName("无查询条件")
    void testJoin(CapturedOutput output) {
        UserCustomJoinQO qo = new UserCustomJoinQO();
        userDao.findAll(qo);


        String outString = output.getOut();
        assertLeftJoinTable(outString, Purse.class);
        assertLeftJoinTable(outString, Order.class);
    }


    @Test
    @DisplayName("根据用户id查询")
    void testJoinWithPurseUserId(CapturedOutput output) {
        UserCustomJoinQO qo = new UserCustomJoinQO();
        qo.setUserId(122L);
        userDao.findAll(qo);

        String outString = output.getOut();
        assertLeftJoinTable(outString, Purse.class);
        assertLeftJoinTable(outString, Order.class);
    }

    @Test
    @DisplayName("join Order orderNo")
    void testUserJoinOrder(CapturedOutput output) {
        UserJoinOrderQO qo = new UserJoinOrderQO();
        qo.setOrderNo("1111");
        userDao.findAll(qo);


        assertQueryCondition(output.getOut(), Order.class, Order_.ORDER_NO);
    }

    @Test
    @DisplayName("orderNo 有则手动改执行join查询")
    void testUserJoinOrderNo2(CapturedOutput output) {
        UserCustomJoinQO2 qo = new UserCustomJoinQO2();
        qo.setOrderNo2("201109UUUU");
        userDao.findAll(qo);

        assertQueryCondition(output.getOut(), Order.class, Order_.ORDER_NO);
    }
}