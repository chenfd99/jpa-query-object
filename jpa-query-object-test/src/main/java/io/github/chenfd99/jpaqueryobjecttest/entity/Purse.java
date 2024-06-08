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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purse_seq")
    @SequenceGenerator(name = "purse_seq", initialValue = 1000)
    @Column(name = "id")
    private Long id;

    @ColumnDefault("0")
    @Column(nullable = false, name = "balance")
    private BigDecimal balance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            referencedColumnName = "id", name = "user_id")
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;
}
