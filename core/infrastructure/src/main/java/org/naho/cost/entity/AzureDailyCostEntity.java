package org.naho.cost.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "azure_daily_costs")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AzureDailyCostEntity extends BaseEntity {

    @Column(name = "record_date", nullable = false, unique = true)
    LocalDate recordDate;

    @Column(name = "cost_amount", precision = 18, scale = 12, nullable = false)
    BigDecimal costAmount;

    @Column(name = "currency", length = 10, nullable = false)
    String currency;
}
