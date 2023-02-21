package io.github.chenfd99.jpaqueryobjecttest.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ChenFD
 */
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


    /**
     * 订单
     * 一对多关系
     * 当qo中使用连表查询时,也会使用到这个字段
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private List<Order> orders;
}
