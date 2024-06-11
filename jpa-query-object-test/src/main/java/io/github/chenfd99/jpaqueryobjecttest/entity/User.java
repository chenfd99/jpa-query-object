package io.github.chenfd99.jpaqueryobjecttest.entity;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ChenFD
 */
@Accessors(chain = true)
@Data
@Entity
@Builder
@Table(name = "t_user")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

    /**
     * If our entities use the GenerationType.IDENTITY identifier generator, Hibernate will silently disable batch inserts.
     */
    @Id
    @SequenceGenerator(name = "user_seq", initialValue = 1000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Long id;

    @Column(length = 64)
    private String name;

    @Column(length = 64)
    private String email;

    @CreatedDate
    private LocalDateTime createdTime;


    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    private Purse purse;


    /**
     * 订单
     * 一对多关系
     * 当qo中使用连表查询时,也会使用到这个字段
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private List<Order> orders;

    public User(String name) {
        this.name = name;
    }
}
