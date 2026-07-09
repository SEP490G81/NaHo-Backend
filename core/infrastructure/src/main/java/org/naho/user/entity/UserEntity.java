package org.naho.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.point.entity.PointHistoryEntity;
import org.naho.point.entity.PointSummaryEntity;
import org.naho.question.entity.QuestionEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.social.report.entity.ReportEntity;
import org.naho.user.type.Gender;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.UserStatus;
import org.naho.user.valueobject.Username;

import java.time.LocalDate;
import java.util.List;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity extends BaseEntity {
    @Column(unique = true, length = Username.MAX_LENGTH)
    String username;

    @Column(unique = true, nullable = false)
    String email;

    @Column(name = "hash_password")
    String hashPassword;

    @Column(name = "full_name")
    String fullName;

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

    @Column(name = "avatar_url", length = 2048)
    String avatarUrl;

    @Column(name = "provider_id", length = 512, unique = true)
    String providerId;

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    List<RoleEntity> roles;

    @OneToMany(mappedBy = "user")
    List<UserSessionEntity> userSessions;

    @OneToMany(mappedBy = "user")
    List<QuestionEntity> questions;

    @OneToMany(mappedBy = "user")
    List<ReportEntity> reports;

    @OneToMany(mappedBy = "user")
    List<PointHistoryEntity> pointHistories;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "point_summary_id")
    PointSummaryEntity pointSummary;
}
