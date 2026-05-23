package org.naho.persistence.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.naho.persistence.BaseEntity;
import org.naho.user.type.PermissionCode;

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
    @Column(unique = true, nullable = false, name = "permission_code")
    PermissionCode permissionCode;

    String description;

    @ManyToMany(mappedBy = "permissions")
    Set<RoleEntity> roles;
}
