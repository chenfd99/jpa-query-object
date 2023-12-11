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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author ChenFD
 */
@DataJpaTest(properties = "spring.jpa.properties.hibernate.format_sql=true")
class UserDaoTest {
    @Autowired
    private UserDao userDao;
    @Autowired
    private OrderDao orderDao;

    @Test
    void IS_NULL() {
        long count = userDao.count();
        System.out.println("count = " + count);

        long usersCount = Stream.generate(() -> userDao.save(new User())).limit(5).count();
        long count1 = userDao.count(UserQO.builder().nameIsNull(true).build());

        System.out.println("count1 = " + count1);
        assertEquals(count1, count + usersCount);
    }

    @Test
    void NOT_NULL() {
        long count = userDao.count();
        System.out.println("count = " + count);

        String name = randomAlphabetic(22);
        long usersCount = Stream.generate(() -> userDao.save(new User(name))).limit(5).count();
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
        long usersCount =
                Stream.generate(() ->
                                userDao.save(new User(RandomUtils.nextInt() % 2 == 0 ? name : null)))
                        .limit(11)
                        .count();
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
        long nameCount =
                Stream.generate(() -> userDao.save(new User(name)))
                        .limit(5)
                        .count();

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
        List<User> userList = Stream.generate(() ->
                        userDao.save(User.builder().name(nameLike + randomAlphabetic(6)).build())
                )
                .limit(10).toList();
        userList.forEach(System.out::println);

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
        List<User> userList = Stream.generate(() -> {
                            User user = User.builder()
                                    .name(nameEqualOrEmailLike)
                                    .build();
                            return userDao.save(user);
                        }
                )
                .limit(6).toList();
        List<User> userList2 = Stream.generate(() -> {
                            User user = User.builder()
                                    .email(nameEqualOrEmailLike + randomAlphabetic(6))
                                    .build();
                            return userDao.save(user);
                        }
                )
                .limit(7).toList();


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
        User user = User.builder()
                .name(randomAlphabetic(11))
                .build();
        userDao.save(user);

        Order order = Order.builder()
                .userId(user.getId())
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
        userDao.save(user);
        Order order = Order.builder()
                .userId(user.getId())
                .orderNo(orderNoOrUsername)
                .build();
        orderDao.save(order);

        List<User> userList = Stream.concat(Stream.generate(() -> userDao.save(new User(orderNoOrUsername))).limit(4), Stream.of(user))
                .toList();

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
        List<User> list1 = Stream.generate(() -> userDao.save(User.builder().name(keyword).build())).limit(6).toList();
        List<User> list = Stream.generate(() -> userDao.save(User.builder().email(keyword).build())).limit(6).toList();


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
        List<User> userList = Stream.generate(() ->
                        userDao.save(new User(randomAlphabetic(6)))
                )
                .limit(10).collect(Collectors.toList());
        userDao.findAll().forEach(System.out::println);

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
                        userDao.save(new User(randomAlphabetic(6)))
                )
                .limit(10).collect(Collectors.toList());
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

        System.out.println("查找后的数据");
        List<User> users = userDao.findAll(UserQO.builder().idNotIn(idList).build());
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