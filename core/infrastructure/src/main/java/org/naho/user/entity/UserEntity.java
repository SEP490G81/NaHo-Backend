package org.naho.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.naho.shared.BaseEntity;
import org.naho.user.type.Gender;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.UserStatus;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity extends BaseEntity {
    // identity
    @Column(unique = true, nullable = false)
    String username;

    @Column(unique = true, nullable = false)
    String email;

    // authentication
    @Column(nullable = false)
    String password;

    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<RoleEntity> roles;

    // profile
    String firstName;
    String lastName;

    @Enumerated(EnumType.STRING)
    Gender gender;

    LocalDate dob; // data of birth
    String avatarUrl;

    @Enumerated(EnumType.STRING)
    JLPTLevel jlptLevel;

    @Enumerated(EnumType.STRING)
    UserStatus status;
}
