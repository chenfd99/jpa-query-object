package io.github.chenfd99.jpaqueryobjecttest.entity;


import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Builder
@Table(name = "t_purse")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Purse {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ColumnDefault("0")
    @Column(nullable = false)
    private BigDecimal balance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            referencedColumnName = "id", name = "userId")
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private User user;
}
