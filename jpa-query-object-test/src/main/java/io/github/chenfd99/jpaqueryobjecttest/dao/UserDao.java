package io.github.chenfd99.jpaqueryobjecttest.dao;

import io.github.chenfd99.jpaqueryobjecttest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * @author ChenFD
 */
@Repository
public interface UserDao extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

}
