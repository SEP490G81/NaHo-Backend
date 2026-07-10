package org.naho.point.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;
import org.naho.user.entity.UserEntity;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "point_summary")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PointSummaryEntity extends BaseEntity {
    @Column(name = "total_point", nullable = false)
    Double totalPoint;

    @OneToOne(mappedBy = "pointSummary")
    UserEntity user;
}
