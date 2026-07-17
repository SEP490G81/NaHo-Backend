package org.naho.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.file.entity.FileEntity;
import org.naho.learning.entity.UserLearningProgressEntity;
import org.naho.learning.entity.UserNodeProgressEntity;
import org.naho.point.entity.PointHistoryEntity;
import org.naho.point.entity.PointSummaryEntity;
import org.naho.season.entity.UserSeasonPointEntity;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.social.report.entity.ReportEntity;
import org.naho.user.type.Gender;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.UserStatus;
import org.naho.user.valueobject.Username;

import java.time.LocalDate;
import java.util.ArrayList;
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

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    List<RoleEntity> roles;

    @OneToMany(mappedBy = "user")
    List<UserSessionEntity> userSessions;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OAuthProviderEntity> oAuthProviders = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    List<SpeakingQuestionEntity> questions;

    @OneToMany(mappedBy = "user")
    List<ReportEntity> reports;

    @OneToMany(mappedBy = "user")
    List<PointHistoryEntity> pointHistories;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "point_summary_id")
    PointSummaryEntity pointSummary;

    @OneToMany(mappedBy = "user")
    List<UserNodeProgressEntity> userNodeProgresses;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_learning_progress_id")
    UserLearningProgressEntity userLearningProgress;

    @OneToMany(mappedBy = "user")
    List<UserSeasonPointEntity> userSeasonPoints;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "avatar_file_id")
    FileEntity avatar;
}
