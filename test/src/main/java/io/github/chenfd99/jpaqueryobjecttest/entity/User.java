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


    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId")
    private List<Order> orders;
}
