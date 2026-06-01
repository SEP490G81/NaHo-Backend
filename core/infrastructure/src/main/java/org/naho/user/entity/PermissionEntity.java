package org.naho.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.naho.shared.persistence.BaseEntity;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permissions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionEntity extends BaseEntity {
    @Column(unique = true, nullable = false, name = "permission_code", length = 100)
    String permissionCode;

    String description;

    @ManyToMany(mappedBy = "permissions")
    Set<RoleEntity> roles;
}
