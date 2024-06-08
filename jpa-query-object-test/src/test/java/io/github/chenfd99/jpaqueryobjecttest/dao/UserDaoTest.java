package io.github.chenfd99.jpaqueryobjecttest.dao;

import io.github.chenfd99.jpaqueryobjecttest.entity.Order;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.qo.UserQO;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author ChenFD
 */
@DataJpaTest
class UserDaoTest {
    @Autowired
    private UserDao userDao;
    @Autowired
    private OrderDao orderDao;

    @Test
    void IS_NULL() {
        long count = userDao.count();
        System.out.println("count = " + count);

        long usersCount = userDao.saveAllAndFlush(Stream.generate(User::new).limit(5).toList()).size();
        long count1 = userDao.count(UserQO.builder().nameIsNull(true).build());

        System.out.println("count1 = " + count1);
        assertEquals(count1, count + usersCount);
    }

    @Test
    void NOT_NULL() {
        long count = userDao.count();
        System.out.println("count = " + count);

        String name = randomAlphabetic(22);
        long usersCount = userDao.saveAllAndFlush(Stream.generate(() -> new User(name)).limit(5).toList()).size();
        System.out.println("usersCount = " + usersCount);

        long count1 = userDao.count(UserQO.builder().nameNotNull(true).build());
        System.out.println("count1 = " + count1);
        assertEquals(count1, count + usersCount);
    }

    @Test
    void EQUAL_OR_NULL() {
        long count = userDao.count();
        System.out.println("count = " + count);

        String name = randomAlphabetic(22);
        Supplier<User> supplier = () -> new User(RandomUtils.nextInt() % 2 == 0 ? name : null);
        List<User> userList = Stream.generate(supplier).limit(11).toList();
        userDao.saveAllAndFlush(userList);
        long usersCount = userList.size();
        System.out.println("usersCount = " + usersCount);

        long count1 = userDao.count(UserQO.builder().nameEqualOrNull(name).build());
        System.out.println("count1 = " + count1);
        assertEquals(count1, count + usersCount);
    }

    @Test
    void NOT_EQUAL_OR_NULL() {
        long count = userDao.count();
        System.out.println("count = " + count);

        String name = randomAlphabetic(22);
        long nameCount = userDao.saveAllAndFlush(Stream.generate(() -> new User(name)).limit(5).toList()).size();


        long nameNullCount =
                Stream.generate(() -> userDao.save(new User()))
                        .limit(6)
                        .count();
        System.out.println("nameCount = " + nameCount);

        long count1 = userDao.count(UserQO.builder().nameNotEqualOrNull(name).build());
        System.out.println("count1 = " + count1);
        assertEquals(count1, count + nameNullCount);
    }


    @Test
    void testQuery() {
        User user = userDao.save(User.builder().name(randomAlphabetic(20)).build());

        List<User> resultList = userDao.findAll(UserQO.builder().username(user.getName()).build());
        resultList.forEach(System.out::println);

        assertEquals(resultList.size(), 1);
        assertEquals(user.getId(), resultList.get(0).getId());
    }

    @Test
    @DisplayName("模糊查询")
    void testLikeQuery() {
        String nameLike = randomAlphabetic(20);
        List<User> userList = Stream.generate(() -> new User(nameLike + randomAlphabetic(6))).limit(111).toList();
        userDao.saveAllAndFlush(userList);

        System.out.println("查询后的数据");
        List<User> resultList = userDao.findAll(UserQO.builder().usernameLike(nameLike).build());
        assertEquals(resultList.size(), userList.size());

        List<Long> idList = userList.stream().map(User::getId).toList();
        for (User user : resultList) {
            System.out.println(user);
            assertTrue(idList.contains(user.getId()));
        }

    }

    @Test
    @DisplayName("用用一个关键词查询多个条件使用or连接")
    void testGroupOr() {
        String nameEqualOrEmailLike = randomAlphabetic(20);
        List<User> userList = Stream.generate(() -> new User(nameEqualOrEmailLike))
                .limit(6).toList();
        userDao.saveAllAndFlush(userList);

        List<User> userList2 = Stream.generate(() -> new User().setEmail(nameEqualOrEmailLike + randomAlphabetic(6)))
                .limit(7).toList();
        userDao.saveAllAndFlush(userList2);

        System.out.println("查询后的数据");
        UserQO qo = UserQO.builder().nameEqualOrEmailLike(nameEqualOrEmailLike).build();
        List<User> resultList = userDao.findAll(qo);
        resultList.forEach(System.out::println);

        assertEquals(resultList.size(), userList.size() + userList2.size());
        qo.setNameEqualOrEmailLike(nameEqualOrEmailLike);
    }


    @Test
    @Transactional
    @DisplayName("连表查询")
    void testJoinQuery() {
        User user = userDao.save(new User().setName(randomAlphabetic(11)));

        Order order = Order.builder()
                .user(user)
                .orderNo("2033_" + randomNumeric(11))
                .build();
        orderDao.save(order);

        System.out.println("user:");
        userDao.findAll().forEach(System.out::println);

        System.out.println("order:");
        orderDao.findAll().forEach(System.out::println);

        System.out.println("result:");
        UserQO qo = UserQO.builder().orderNo(order.getOrderNo()).build();
        List<User> users = userDao.findAll(qo);
        users.forEach(System.out::println);

        assertEquals(1, users.size());
        assertEquals(users.get(0).getId(), user.getId());
    }

    @Test
    @DisplayName("订单连表和名称相等查询")
    void testJoinOrEqualQuery() {
        var orderNoOrUsername = randomAlphabetic(20);
        User user = User.builder()
                .name(randomAlphabetic(11))
                .build();

        Stream<User> userStream = Stream.generate(() -> new User(orderNoOrUsername)).limit(4);
        List<User> userList = Stream.concat(userStream, Stream.of(user)).toList();
        userDao.saveAll(userList);

        Order order = Order.builder()
                .user(user)
                .orderNo(orderNoOrUsername)
                .build();
        orderDao.save(order);

        System.out.println("user:");
        userDao.findAll().forEach(System.out::println);

        System.out.println("order:");
        orderDao.findAll().forEach(System.out::println);

        System.out.println("result:");
        UserQO qo = UserQO.builder().orderNoOrUsername(order.getOrderNo()).build();
        List<User> users = userDao.findAll(qo);
        users.forEach(System.out::println);

        assertEquals(userList.size(), users.size());
        userList.forEach(u -> assertTrue(users.stream().map(User::getId).toList().contains(u.getId())));
    }


    @Test
    @DisplayName("用用一个关键词查询多个条件使用and连接")
    void testGroupAnd() {
        String keyword = randomAlphabetic(20);
        User user = User.builder().name(keyword).email(keyword).build();
        userDao.save(user);

        //干扰数据
        userDao.saveAllAndFlush(Stream.generate(() -> new User(keyword)).limit(6).toList());
        userDao.saveAllAndFlush(Stream.generate(() -> new User().setEmail(keyword)).limit(6).toList());


        System.out.println("查询后的数据");
        UserQO qo = UserQO.builder().nameEqualAndEmailEqual(keyword).build();
        List<User> resultList = userDao.findAll(qo);
        resultList.forEach(System.out::println);

        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0).getId(), user.getId());
    }


    @Test
    @DisplayName("In 查询")
    void testIn() {
        System.out.println("准备的数据");
        List<User> userList = Stream.generate(() -> new User(randomAlphabetic(6)))
                .limit(10).collect(Collectors.toList());
        userDao.saveAllAndFlush(userList);

        Collections.shuffle(userList);
        List<Long> idList = userList.stream()
                .map(User::getId).limit(8)
                .toList();

        System.out.println("查询的 In 的id列表:");
        String idsStr = idList.stream().map(Object::toString).collect(Collectors.joining(",\t"));
        System.out.println(idsStr);

        System.out.println("查找后的数据");
        List<User> users = userDao.findAll(UserQO.builder().idIn(idList).build());
        for (User user : users) {
            System.out.println(user);
            assertTrue(idList.contains(user.getId()));
        }

        assertEquals(idList.size(), users.size());
    }

    @Test
    @DisplayName("NotIn 查询")
    void testNotIn() {
        System.out.println("准备的数据");
        List<User> userList = Stream.generate(() ->
                        new User(randomAlphabetic(6)))
                .limit(10).collect(Collectors.toList());
        userDao.saveAllAndFlush(userList);

        List<User> totalUserList = userDao.findAll();
        totalUserList.forEach(System.out::println);
        System.out.println();

        Collections.shuffle(userList);
        List<Long> idList = userList.stream()
                .map(User::getId).skip(4)
                .toList();

        System.out.println("查询的 notIn 的id列表:");
        String idsStr = idList.stream().map(Object::toString).collect(Collectors.joining(",\t"));
        System.out.println(idsStr);

        List<User> users = userDao.findAll(UserQO.builder().idNotIn(idList).build());
        System.out.println("查找后的数据");
        for (User user : users) {
            System.out.println(user);
            assertFalse(idList.contains(user.getId()));
        }

        assertEquals(totalUserList.size() - idList.size(), users.size());
    }

    @Test
    @DisplayName("Not Equal")
    void testNotEqual() {
        long count = userDao.count();
        String name = randomAlphabetic(22);
        userDao.save(new User(name));
        userDao.save(new User(name));
        long count1 = userDao.count(UserQO.builder().nameNotEqual(name).build());
        assertEquals(count1, count);
    }

    @Test
    @DisplayName("无条件查询")
    void testNoRestriction() {
        userDao.findAll(new UserQO());
    }

}