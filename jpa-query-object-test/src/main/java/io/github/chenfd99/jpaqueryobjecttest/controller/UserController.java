package io.github.chenfd99.jpaqueryobjecttest.controller;

import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import io.github.chenfd99.jpaqueryobjecttest.qo.UserJoinQO;
import io.github.chenfd99.jpaqueryobjecttest.qo.UserQO;
import io.github.chenfd99.jpaqueryobjecttest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * QueryObject使用示例
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    /**
     * 分页查询
     *
     * @param qo       查询条件
     * @param pageable 分页参数
     */
    @GetMapping("/page")
    public Page<User> page(UserJoinQO qo, Pageable pageable) {
        return userService.page(qo, pageable);
    }


    /**
     * 查询全部用户数据
     *
     * @param qo   查询条件
     * @param sort 排序
     */
    @GetMapping("/list")
    public List<User> list(UserQO qo, Sort sort) {
        return userService.list(qo, sort);
    }


    /**
     * 创建用户
     */
    @GetMapping("/create/{username}")
    public User create(@PathVariable String username) {
        return userService.create(username);
    }


}
