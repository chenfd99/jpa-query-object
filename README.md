# jpa动态查询实体

用于动态接收jpa查询条件，实现原理，通过继承`Specification`
，并使用注解标明需要参与动态生成查询条件的的字段，然后在`toPredicate`里生成动态的查询条件，
使用的是`JpaSpecificationExecutor`里的`findAll`方法查询，所以这里的dao得继承`JpaSpecificationExecutor`。
这样做可以简化动态查询条件的代码编写量。并且对于jpa项目的侵入性很小，对现有项目的改造工作量很小，无缝接入现有jpa项目。

### 使用场景

1. controller接收页面动态参数。
2. service里编写动态查询条件的业务代码

## 依赖

如果项目是springboot3.0以上，得在依赖里加上`<classifier>jakarta</classifier>`。

```xml

<dependency>
    <groupId>io.github.chenfd99</groupId>
    <artifactId>jpa-query-object</artifactId>
    <version>0.9.9</version>
    <!--    <classifier>jakarta</classifier>-->
</dependency>
```

## 使用方法

1. 用于查询的实体继承 `QueryObject`；
2. 并在需要参与查询的字段上添加注解`@QFiled`；

当实体的字段值不为null时，会在`toPredicate`里动态加入条件，
同一个字段可使用多个`@QFiled`注解，多个`@QFiled`注解的字段在生成查询条件时会默认使用`or`连接起来。如：

```text
//数据库实体
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
//Dao
@Repository
public interface UserDao extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

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

//使用方法
@Test
void testQuery() {
    UserQO qo = new UserQO();
    qo.setUsername("chen");
    qo.setKeyword("chen11");
    qo.setCreatedTimeLE(LocalDateTime.now());
    userDao.findAll(qo);
}

//生成查询条件
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

## 注解说明

### 单字段使用单个`@QFiled`注解, 生成单个查询条件

```text
    /**
     * 用户名
     */
    @QFiled(name = "name", value = QType.EQUAL)
    private String username;
    
    //使用方法
    UserQO qo = new UserQO();
    qo.setUsername("chen");
    userDao.findAll(qo);
    
    //生成查询条件
    select
        user0_.id as id1_1_,
        user0_.created_time as created_2_1_,
        user0_.email as email3_1_,
        user0_.name as name4_1_ 
    from
        t_user user0_ 
    where
        user0_.name=?
```

### 同一个字段使用`@QGroup`注解并且`type=QGroup.Type.OR`

同一个字段使用`@QGroup`注解并且`type=QGroup.Type.OR`, 在生成sql查询条件时, 会使用`or`连接. 当一字段使用多个`QFiled`
注解时,也可视为是使用`@QGroup(type=QGroup.Type.OR)`. 如:

```text
    /**
     * 查询条件为 用户名或者邮箱
     */
    @QFiled(name = "name", value = QType.EQUAL)
    @QFiled(name = "email", value = QType.LIKE)
    private String keyword;
    
    //使用方法
    UserQO qo = new UserQO();
    qo.setKeyword("chen");
    userDao.findAll(qo);
    
    //生成查询条件
    select
        user0_.id as id1_1_,
        user0_.created_time as created_2_1_,
        user0_.email as email3_1_,
        user0_.name as name4_1_ 
    from
        t_user user0_ 
    where
        user0_.name=? 
        or user0_.email like ?
```

### 同一个字段使用`@QGroup`注解, 并且`type=QGroup.Type.AND`

同一个字段使用`@QGroup`注解, 并且`type=QGroup.Type.AND`,在生成查询条件时, 使用`and`连接. 如:

```text
    //注解字段
    @QGroup(
            value = {@QFiled(name = "name"), @QFiled(name = "email")},
            type = QGroup.Type.AND
    )
    private String groupAnd;
    
    //使用方法
    UserQO qo = new UserQO();
    qo.setGroupAnd("chen");
    userDao.findAll(qo);
    
    //生成查询的条件
    select
        user0_.id as id1_1_,
        user0_.created_time as created_2_1_,
        user0_.email as email3_1_,
        user0_.name as name4_1_ 
    from
        t_user user0_ 
    where
        user0_.name=? 
        and user0_.email=?
```

## 联表查询

联表查询需要在数据库实体中定义实体与实体之间的关联关系,并添加`@OneToMany`等关联注解。

```text
/**
 * 订单
 * 一对多关系
 * 当qo中使用连表查询时,也会使用到这个字段
 */
@OneToMany(fetch = FetchType.LAZY)
@JoinColumn(name = "memberId")
private List<Order> orders;

//查询实体注解
/**
 * 订单号
 */
@QFiled(joinName = "orders")
private String orderNo;

//使用方法
UserQO qo = new UserQO();
qo.setOrderNo("20231212xxxxxxxx");
userDao.findAll(qo);

//生成的查询条件
select
    user0_.id as id1_1_,
    user0_.created_time as created_2_1_,
    user0_.email as email3_1_,
    user0_.name as name4_1_ 
from
    t_user user0_ 
inner join
    t_order orders1_ 
        on user0_.id=orders1_.member_id 
where
    orders1_.order_no=?
```

具体使用方法请看test模块