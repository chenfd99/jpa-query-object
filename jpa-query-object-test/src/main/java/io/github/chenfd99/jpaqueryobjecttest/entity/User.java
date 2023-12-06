package io.github.chenfd99.jpaqueryobjecttest.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ChenFD
 */
@Data
@Entity
@Builder
@Table(name = "t_user")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64)
    private String name;

    @Column(length = 64)
    private String email;

    @CreatedDate
    private LocalDateTime createdTime;


    /**
     * 订单
     * 一对多关系
     * 当qo中使用连表查询时,也会使用到这个字段
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private List<Order> orders;

    public User(String name) {
        this.name = name;
    }
}
