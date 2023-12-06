package io.github.chenfd99.jpaqueryobjecttest.entity;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "t_order")
@EntityListeners(AuditingEntityListener.class)
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     * 订单号
     */
    @Column(length = 64)
    private String orderNo;

    /**
     * 用户id
     */
    private Long userId;

    @CreatedDate
    private LocalDateTime createdTime;
}
