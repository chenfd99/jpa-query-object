# jpa查询实体

实现原理,通过继承`Specification`,并使用注解标明要查询的字段,最后实现`toPredicate`方法. 简化Specification代码编写量.


```xml

<dependency>
    <groupId>io.github.chenfd99</groupId>
    <artifactId>jpa-query-object</artifactId>
    <version>0.9.1</version>
</dependency>
```

### 代码

```java
//实体
@Data
@Entity
@Table(name = "user")
public class User extends BaseModel {
    private String username;
    private String email;

    @CreatedDate
    @Column(updatable = false)
    protected LocalDateTime createdTime;

    public User(String username) {
        this.username = username;
    }

}

@Repository
public interface UserRepository extends JpaSpecificationExecutor<User> {
}


//查询实体
@Data
public class UserSearch extends QueryObject<User> {

    @QFiled(name = User_.USERNAME, value = QType.EQUAL)
    @QFiled(name = User_.EMAIL, value = QType.LIKE)
    private String username;

    @QFiled(name = User_.CREATED_TIME, value = QType.LESS_THAN_OR_EQUAL)
    private LocalDateTime createdTime;
}
```

#### 查询方法

```java

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired
    private UserRoleRepository userRoleDao;

    @Test
    void testQueryObject() {
        UserSearch search = new UserSearch();
        search.setCreatedTime(LocalDateTime.now());
        search.setUsername("chen");
        List<User> users = userDao.findAll(search);
    }
}
```

#### 输出语句

```text
Hibernate: 
    select
        user0_.id as id1_7_,
        user0_.created_by as created_2_7_,
        user0_.created_time as created_3_7_,
        user0_.updated_by as updated_4_7_,
        user0_.updated_time as updated_5_7_,
        user0_.version as version6_7_,
        user0_.deleted_time as deleted_7_7_,
        user0_.email as email8_7_,
        user0_.username as username9_7_ 
    from
        user user0_ 
    where
        (
            user0_.username=? 
            or user0_.email like ?
        ) 
        and user0_.created_time<=?
binding parameter [1] as [VARCHAR] - [chen]
binding parameter [2] as [VARCHAR] - [%chen%]
binding parameter [3] as [TIMESTAMP] - [2022-12-09T23:47:39.538588200]

```