package io.github.chenfd99.jpaqueryobjecttest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.LocalDateTime;

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
}
