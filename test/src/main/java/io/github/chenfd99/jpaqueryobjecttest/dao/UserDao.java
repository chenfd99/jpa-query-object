package io.github.chenfd99.jpaqueryobjecttest.dao;

import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;


/**
 * @author ChenFD
 */
@org.springframework.stereotype.Repository
public interface UserDao extends Repository<User, Long>, JpaSpecificationExecutor<User> {

}
