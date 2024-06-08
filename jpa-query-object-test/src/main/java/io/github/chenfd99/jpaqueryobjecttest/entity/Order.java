package io.github.chenfd99.jpaqueryobjecttest.entity;


import io.github.chenfd99.jpaqueryobjecttest.status.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "t_order")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Order implements Serializable {

    @Id
    @SequenceGenerator(name = "order_seq", initialValue = 1000)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @Column(name = "id")
    private Long id;


    /**
     * 订单号
     */
    @Column(length = 64, name = "order_no")
    private String orderNo;

    /**
     * 用户id
     */
    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;


    @CreatedDate
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;


    @Enumerated(EnumType.STRING)
    private OrderStatus status;


}
