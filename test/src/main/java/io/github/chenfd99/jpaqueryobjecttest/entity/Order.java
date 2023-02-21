package io.github.chenfd99.jpaqueryobjecttest.entity;


import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
