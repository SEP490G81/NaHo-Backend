package org.naho.persistence.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.naho.persistence.BaseEntity;
import org.naho.persistence.file.model.FileEntity;
import org.naho.user.type.Gender;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.UserStatus;
import org.naho.user.valueobject.Username;

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
    @Column(unique = true, nullable = false, length = Username.MAX_LENGTH)
    String username;

    @Column(unique = true, nullable = false)
    String email;

    // authentication
    @Column(name = "hash_password", nullable = false)
    String hashPassword;

    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<RoleEntity> roles;

    // profile
    @Column(name = "first_name")
    String firstName;

    @Column(name = "last_name")
    String lastName;

    @Enumerated(EnumType.STRING)
    Gender gender;

    LocalDate dob; // data of birth

    @OneToOne
    @JoinColumn(name = "avatar_file_id")
    FileEntity avatar;

    @Column(name = "jlpt_level", nullable = false)
    @Enumerated(EnumType.STRING)
    JLPTLevel jlptLevel;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    UserStatus status;
}
