package org.naho.user.model;

import org.naho.user.type.Gender;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.UserStatus;
import org.naho.user.valueobject.Dob;
import org.naho.user.valueobject.Email;
import org.naho.user.valueobject.Username;

import java.util.List;

public class User {

    private final Long id;
    private final List<Long> roleIds;
    private final List<Long> userSessionIds;
    private final List<Long> oAuthProviderIds;
    private final List<Long> speakingQuestionIds;
    private final List<Long> reportIds;
    private final List<Long> pointHistoryIds;
    private final List<Long> userNodeProgressIds;
    private final List<Long> userDailyAttendanceIds;
    private final List<Long> userDailyMissionIds;
    private Long avatarFileId;
    private final Long userLearningProgressId;

    private Username username;
    private final Email email;
    private String hashPassword;
    private String fullName;
    private Gender gender;
    private Dob dob;
    private JLPTLevel jlptLevel;
    private UserStatus status;
    private boolean isEmailVerified;

    private User(Builder builder) {
        this.id = builder.id;
        this.roleIds = builder.roleIds;
        this.userSessionIds = builder.userSessionIds;
        this.oAuthProviderIds = builder.oAuthProviderIds;
        this.speakingQuestionIds = builder.speakingQuestionIds;
        this.reportIds = builder.reportIds;
        this.pointHistoryIds = builder.pointHistoryIds;
        this.userNodeProgressIds = builder.userNodeProgressIds;
        this.userDailyAttendanceIds = builder.userDailyAttendanceIds;
        this.userDailyMissionIds = builder.userDailyMissionIds;
        this.userLearningProgressId = builder.userLearningProgressId;
        this.avatarFileId = builder.avatarFileId;
        this.username = builder.username;
        this.email = builder.email;
        this.hashPassword = builder.hashPassword;
        this.fullName = builder.fullName;
        this.gender = builder.gender;
        this.dob = builder.dob;
        this.jlptLevel = builder.jlptLevel;
        this.status = builder.status;
        this.isEmailVerified = builder.isEmailVerified;
    }

    public static User registerNewUser(String rawUsername, String hashPassword, String rawEmail, List<Long> roleIds) {
        return User.builder()
                .username(Username.of(rawUsername))
                .email(Email.of(rawEmail))
                .hashPassword(hashPassword)
                .status(UserStatus.ACTIVE)
                .isEmailVerified(false)
                .roleIds(roleIds)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void verifyEmail() {
        this.isEmailVerified = true;
    }

    public Builder toBuilder() {
        return builder()
                .id(id)
                .roleIds(roleIds)
                .userSessionIds(userSessionIds)
                .oAuthProviderIds(oAuthProviderIds)
                .speakingQuestionIds(speakingQuestionIds)
                .reportIds(reportIds)
                .pointHistoryIds(pointHistoryIds)
                .userNodeProgressIds(userNodeProgressIds)
                .userDailyAttendanceIds(userDailyAttendanceIds)
                .userDailyMissionIds(userDailyMissionIds)
                .userLearningProgressId(userLearningProgressId)
                .avatarFileId(avatarFileId)
                .username(username)
                .email(email)
                .hashPassword(hashPassword)
                .fullName(fullName)
                .gender(gender)
                .dob(dob)
                .jlptLevel(jlptLevel)
                .status(status);
    }

    public Long getId() {
        return id;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public List<Long> getUserSessionIds() {
        return userSessionIds;
    }

    public List<Long> getOAuthProviderIds() {
        return oAuthProviderIds;
    }

    public List<Long> getSpeakingQuestionIds() {
        return speakingQuestionIds;
    }

    public List<Long> getReportIds() {
        return reportIds;
    }

    public List<Long> getPointHistoryIds() {
        return pointHistoryIds;
    }

    public List<Long> getUserNodeProgressIds() {
        return userNodeProgressIds;
    }

    public List<Long> getUserDailyAttendanceIds() {
        return userDailyAttendanceIds;
    }

    public List<Long> getUserDailyMissionIds() {
        return userDailyMissionIds;
    }

    public Long getUserLearningProgressId() {
        return userLearningProgressId;
    }

    public Long getAvatarFileId() {
        return avatarFileId;
    }

    public void setAvatarFileId(Long avatarFileId) {
        this.avatarFileId = avatarFileId;
    }

    public Username getUsername() {
        return username;
    }

    public void setUsername(Username username) {
        this.username = username;
    }

    public Email getEmail() {
        return email;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Dob getDob() {
        return dob;
    }

    public void setDob(Dob dob) {
        this.dob = dob;
    }

    public JLPTLevel getJlptLevel() {
        return jlptLevel;
    }

    public void setJlptLevel(JLPTLevel jlptLevel) {
        this.jlptLevel = jlptLevel;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public static final class Builder {

        private Long id;
        private List<Long> roleIds;
        private List<Long> userSessionIds;
        private List<Long> oAuthProviderIds;
        private List<Long> speakingQuestionIds;
        private List<Long> reportIds;
        private List<Long> pointHistoryIds;
        private List<Long> userNodeProgressIds;
        private List<Long> userDailyAttendanceIds;
        private List<Long> userDailyMissionIds;

        private Long userLearningProgressId;
        private Long avatarFileId;

        private Username username;
        private Email email;
        private String hashPassword;
        private String fullName;
        private Gender gender;
        private Dob dob;
        private JLPTLevel jlptLevel;

        private UserStatus status;
        private boolean isEmailVerified;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder roleIds(List<Long> roleIds) {
            this.roleIds = roleIds;
            return this;
        }

        public Builder userSessionIds(List<Long> userSessionIds) {
            this.userSessionIds = userSessionIds;
            return this;
        }

        public Builder oAuthProviderIds(List<Long> oAuthProviderIds) {
            this.oAuthProviderIds = oAuthProviderIds;
            return this;
        }

        public Builder speakingQuestionIds(List<Long> speakingQuestionIds) {
            this.speakingQuestionIds = speakingQuestionIds;
            return this;
        }

        public Builder reportIds(List<Long> reportIds) {
            this.reportIds = reportIds;
            return this;
        }

        public Builder pointHistoryIds(List<Long> pointHistoryIds) {
            this.pointHistoryIds = pointHistoryIds;
            return this;
        }

        public Builder userNodeProgressIds(List<Long> userNodeProgressIds) {
            this.userNodeProgressIds = userNodeProgressIds;
            return this;
        }

        public Builder userDailyAttendanceIds(List<Long> userDailyAttendanceIds) {
            this.userDailyAttendanceIds = userDailyAttendanceIds;
            return this;
        }

        public Builder userDailyMissionIds(List<Long> userDailyMissionIds) {
            this.userDailyMissionIds = userDailyMissionIds;
            return this;
        }

        public Builder userLearningProgressId(Long userLearningProgressId) {
            this.userLearningProgressId = userLearningProgressId;
            return this;
        }

        public Builder avatarFileId(Long avatarFileId) {
            this.avatarFileId = avatarFileId;
            return this;
        }

        public Builder username(Username username) {
            this.username = username;
            return this;
        }

        public Builder email(Email email) {
            this.email = email;
            return this;
        }

        public Builder hashPassword(String hashPassword) {
            this.hashPassword = hashPassword;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder dob(Dob dob) {
            this.dob = dob;
            return this;
        }

        public Builder jlptLevel(JLPTLevel jlptLevel) {
            this.jlptLevel = jlptLevel;
            return this;
        }

        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public Builder isEmailVerified(boolean isEmailVerified) {
            this.isEmailVerified = isEmailVerified;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}