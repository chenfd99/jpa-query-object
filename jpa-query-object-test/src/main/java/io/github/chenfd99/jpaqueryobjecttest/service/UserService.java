package io.github.chenfd99.jpaqueryobjecttest.service;

import io.github.chenfd99.jpaqueryobject.base.QueryObject;
import io.github.chenfd99.jpaqueryobjecttest.dao.UserDao;
import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;

    /**
     * 分页查询
     *
     * @param qo       查询条件
     * @param pageable 分页参数
     */
    public <QO extends QueryObject<User>> Page<User> page(QO qo, Pageable pageable) {
        return userDao.findAll(qo, pageable);
    }


    /**
     * 查询全部用户数据
     *
     * @param qo   查询条件
     * @param sort 排序
     */
    public <QO extends QueryObject<User>> List<User> list(QO qo, Sort sort) {
        return userDao.findAll(qo, sort);
    }


    public User create(String username) {
        User user = new User();
        user.setName(username);
        return userDao.save(user);
    }

}
