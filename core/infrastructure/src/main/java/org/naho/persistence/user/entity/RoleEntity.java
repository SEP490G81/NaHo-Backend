package org.naho.persistence.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.naho.persistence.BaseEntity;
import org.naho.user.type.RoleName;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleEntity extends BaseEntity {
    @Column(unique = true, nullable = false, name = "role_name")
    @Enumerated(EnumType.STRING)
    RoleName roleName;

    String description;

    @ManyToMany(mappedBy = "roles")
    Set<UserEntity> users;

    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    Set<PermissionEntity> permissions;
}
