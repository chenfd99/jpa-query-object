package io.github.chenfd99.jpaqueryobjecttest.dao;

import io.github.chenfd99.jpaqueryobjecttest.base.BaseJoinTest;
import io.github.chenfd99.jpaqueryobjecttest.entity.Order;
import io.github.chenfd99.jpaqueryobjecttest.entity.Order_;
import io.github.chenfd99.jpaqueryobjecttest.entity.Purse;
import io.github.chenfd99.jpaqueryobjecttest.entity.Purse_;
import io.github.chenfd99.jpaqueryobjecttest.qo.UserForceJoinQO;
import io.github.chenfd99.jpaqueryobjecttest.qo.UserJoinQO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.math.BigDecimal;

/**
 * @author ChenFD
 */
@DataJpaTest
@ExtendWith(OutputCaptureExtension.class)
class UserDaoJoinTest implements BaseJoinTest {
    @Autowired
    private UserDao userDao;

    @Test
    @DisplayName("无查询条件")
    void testJoin(CapturedOutput output) {
        UserForceJoinQO qo = new UserForceJoinQO();
        userDao.findAll(qo);

        assertInnerJoinTable(output.getOut(), Order.class);
        assertLeftJoinTable(output.getOut(), Purse.class);
    }


    @Test
    @DisplayName("根据钱包用户id查询")
    void testJoinWithPurseUserId(CapturedOutput output) {
        UserJoinQO qo = new UserJoinQO();
        qo.setPurseUserId(122L);
        userDao.findAll(qo);

        assertQueryCondition(output, Purse.class, Purse_.USER_ID);
    }


    @Test
    @DisplayName("其他条件")
    void testJoinWithOther(CapturedOutput output) {
        UserJoinQO qo = new UserJoinQO();
        qo.setOrderNo("1111");
        qo.setPurseBalanceGE(BigDecimal.valueOf(100.0));
        userDao.findAll(qo);

        assertQueryCondition(output, Order.class, Order_.ORDER_NO);
        assertQueryCondition(output, Purse.class, Purse_.BALANCE);
    }

}