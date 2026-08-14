package org.naho.speech.llm.entity;

import jakarta.persistence.*;
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
@Table(
        name = "openai_daily_costs",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_openai_daily_costs_date", columnNames = {"record_date"})
        }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OpenAiDailyCostEntity extends BaseEntity {

    @Column(name = "record_date", nullable = false, unique = true)
    LocalDate recordDate;

    @Column(name = "cost_amount", nullable = false, precision = 12, scale = 6)
    BigDecimal costAmount;

    @Builder.Default
    @Column(name = "currency", nullable = false, length = 10)
    String currency = "USD";
}
