package io.github.chenfd99.jpaqueryobjecttest.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_order")
public class Order implements Serializable {

    @Id
    private Long id;


    /**
     * 订单号
     */
    private String orderNo;


    private Long memberId;

    @CreatedDate
    private LocalDateTime createdTime;
}
