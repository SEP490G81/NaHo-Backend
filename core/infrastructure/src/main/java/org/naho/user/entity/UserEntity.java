package org.naho.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.naho.file.model.FileEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.user.type.AccountType;
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
    @Column(unique = true, nullable = false, length = Username.MAX_LENGTH)
    String username;

    @Column(unique = true, nullable = false)
    String email;

    @Column(name = "hash_password", nullable = false)
    String hashPassword;

    @Column(name = "refresh_token", length = 512)
    String refreshToken;

    @Column(name = "account_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    AccountType accountType;

    @Column(name = "first_name", length = 100)
    String firstName;

    @Column(name = "last_name", length = 100)
    String lastName;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    Gender gender;

    LocalDate dob; // data of birth

    @Column(name = "jlpt_level", nullable = false, length = 2)
    @Enumerated(EnumType.STRING)
    JLPTLevel jlptLevel;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    UserStatus status;

    @Column(name = "current_streak")
    Integer currentStreak;

    @Column(name = "longest_streak")
    Integer longestStreak;

    @Column(name = "last_practice_date")
    LocalDate lastPracticeDate;

    @OneToOne
    @JoinColumn(name = "avatar_file_id")
    FileEntity avatarFile;

    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<RoleEntity> roles;
}
