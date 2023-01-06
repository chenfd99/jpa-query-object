# jpa查询实体

实现原理,通过继承`Specification`,并使用注解标明要查询的字段,最后实现`toPredicate`方法. 简化Specification代码编写量.

### springboot3.0 以下

```xml

<dependency>
    <groupId>io.github.chenfd99</groupId>
    <artifactId>jpa-query-object</artifactId>
    <version>0.9.2</version>
</dependency>
```

### springboot3.0及以上

这个版本需要java17及以上

```xml

<dependency>
    <groupId>io.github.chenfd99</groupId>
    <artifactId>jpa-query-object</artifactId>
    <version>0.9.8</version>
</dependency>
```

### 代码

```java
//实体
@Data
@Entity
@Table(name = "t_user")
public class User implements Serializable {
    @Id
    private Long id;

    private String name;

    private String email;

    @CreatedDate
    private LocalDateTime createdTime;
}

@org.springframework.stereotype.Repository
public interface UserDao extends Repository<User, Long>, JpaSpecificationExecutor<User> {

}


//查询实体
@Setter
@Getter
public class UserQO extends QueryObject<User> {
    @QFiled(name = "name", value = QType.EQUAL)
    private String username;

    @QFiled(name = "createdTime", value = QType.LESS_THAN_OR_EQUAL)
    private LocalDateTime createdTimeLE;


    @QFiled(name = "name", value = QType.EQUAL)
    @QFiled(name = "email", value = QType.LIKE)
    private String keyword;
}
```

#### 查询方法

```java

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

}
```

#### 输出语句

```text
    select
        u1_0.id,
        u1_0.created_time,
        u1_0.email,
        u1_0.name 
    from
        t_user u1_0 
    where
        u1_0.name=? 
        and u1_0.created_time<=? 
        and (
            u1_0.name=? 
            or u1_0.email like ?
        )
```