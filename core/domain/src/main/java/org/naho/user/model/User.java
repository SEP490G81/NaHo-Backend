package org.naho.user.model;

import org.naho.user.type.Gender;
import org.naho.user.type.UserStatus;
import org.naho.user.valueobject.Dob;
import org.naho.user.valueobject.Email;
import org.naho.user.valueobject.Username;

import java.time.Instant;
import java.util.List;

public class User {

    public static final int MAX_FAILED_ATTEMPTS = 5;
    public static final int LOCK_DURATION_MINUTES = 15;

    private final Long id;
    private final Long roleId;
    private final List<Long> userSessionIds;
    private final List<Long> authProviderIds;
    private final List<Long> speakingQuestionIds;
    private final List<Long> reportIds;
    private final List<Long> pointHistoryIds;
    private final List<Long> userNodeProgressIds;
    private final List<Long> userDailyAttendanceIds;
    private final List<Long> userDailyMissionIds;
    private final List<Long> userSubscriptionIds;
    private final List<Long> userDailyAiUsageIds;
    private final Long userLearningProgressId;
    private final Email email;
    private Long avatarFileId;
    private Username username;
    private String hashPassword;
    private String fullName;
    private Gender gender;
    private Dob dob;
    private UserStatus status;
    private boolean isEmailVerified;
    private Integer failedLoginAttemptCount;
    private Instant lockedUntil;

    private User(Builder builder) {
        this.id = builder.id;
        this.roleId = builder.roleId;
        this.userSessionIds = builder.userSessionIds;
        this.authProviderIds = builder.authProviderIds;
        this.speakingQuestionIds = builder.speakingQuestionIds;
        this.reportIds = builder.reportIds;
        this.pointHistoryIds = builder.pointHistoryIds;
        this.userNodeProgressIds = builder.userNodeProgressIds;
        this.userDailyAttendanceIds = builder.userDailyAttendanceIds;
        this.userDailyMissionIds = builder.userDailyMissionIds;
        this.userSubscriptionIds = builder.userSubscriptionIds;
        this.userDailyAiUsageIds = builder.userDailyAiUsageIds;
        this.userLearningProgressId = builder.userLearningProgressId;
        this.avatarFileId = builder.avatarFileId;
        this.username = builder.username;
        this.email = builder.email;
        this.hashPassword = builder.hashPassword;
        this.fullName = builder.fullName;
        this.gender = builder.gender;
        this.dob = builder.dob;
        this.status = builder.status;
        this.isEmailVerified = builder.isEmailVerified;
        this.failedLoginAttemptCount = builder.failedLoginAttemptCount;
        this.lockedUntil = builder.lockedUntil;
    }

    public static User registerNewUser(String rawUsername, String fullName, String hashPassword, String rawEmail, Long roleId) {
        return User.builder()
                .username(Username.of(rawUsername))
                .fullName(fullName)
                .email(Email.of(rawEmail))
                .hashPassword(hashPassword)
                .status(UserStatus.ACTIVE)
                .isEmailVerified(false)
                .roleId(roleId)
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
                .roleId(roleId)
                .userSessionIds(userSessionIds)
                .authProviderIds(authProviderIds)
                .speakingQuestionIds(speakingQuestionIds)
                .reportIds(reportIds)
                .pointHistoryIds(pointHistoryIds)
                .userNodeProgressIds(userNodeProgressIds)
                .userDailyAttendanceIds(userDailyAttendanceIds)
                .userDailyMissionIds(userDailyMissionIds)
                .userSubscriptionIds(userSubscriptionIds)
                .userDailyAiUsageIds(userDailyAiUsageIds)
                .userLearningProgressId(userLearningProgressId)
                .avatarFileId(avatarFileId)
                .username(username)
                .email(email)
                .hashPassword(hashPassword)
                .fullName(fullName)
                .gender(gender)
                .dob(dob)
                .status(status)
                .isEmailVerified(isEmailVerified)
                .failedLoginAttemptCount(failedLoginAttemptCount)
                .lockedUntil(lockedUntil);
    }

    public Long getId() {
        return id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public List<Long> getUserSessionIds() {
        return userSessionIds;
    }

    public List<Long> getAuthProviderIds() {
        return authProviderIds;
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

    public List<Long> getUserSubscriptionIds() {
        return userSubscriptionIds;
    }

    public List<Long> getUserDailyAiUsageIds() {
        return userDailyAiUsageIds;
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


    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Integer getFailedLoginAttemptCount() {
        return failedLoginAttemptCount;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    /**
     * Kiểm tra tài khoản có đang bị khoá tạm thời hay không.
     *
     * @param now thời điểm hiện tại
     * @return true nếu tài khoản đang bị khoá
     */
    public boolean isLocked(Instant now) {
        return lockedUntil != null && now.isBefore(lockedUntil);
    }

    /**
     * Tăng số lần đăng nhập thất bại. Nếu đạt ngưỡng MAX_FAILED_ATTEMPTS,
     * tài khoản sẽ bị khoá tạm thời trong LOCK_DURATION_MINUTES phút.
     *
     * @param now thời điểm hiện tại
     */
    public void incrementFailedLoginAttempt(Instant now) {
        int count = (this.failedLoginAttemptCount == null ? 0 : this.failedLoginAttemptCount) + 1;
        this.failedLoginAttemptCount = count;
        if (count >= MAX_FAILED_ATTEMPTS) {
            this.lockedUntil = now.plusSeconds((long) LOCK_DURATION_MINUTES * 60);
            this.failedLoginAttemptCount = 0;
        }
    }

    /**
     * Reset số lần đăng nhập thất bại sau khi đăng nhập thành công.
     */
    public void resetFailedLoginAttempt() {
        this.failedLoginAttemptCount = 0;
        this.lockedUntil = null;
    }

    public static final class Builder {

        private Long id;
        private Long roleId;
        private List<Long> userSessionIds;
        private List<Long> authProviderIds;
        private List<Long> speakingQuestionIds;
        private List<Long> reportIds;
        private List<Long> pointHistoryIds;
        private List<Long> userNodeProgressIds;
        private List<Long> userDailyAttendanceIds;
        private List<Long> userDailyMissionIds;
        private List<Long> userSubscriptionIds;
        private List<Long> userDailyAiUsageIds;

        private Long userLearningProgressId;
        private Long avatarFileId;

        private Username username;
        private Email email;
        private String hashPassword;
        private String fullName;
        private Gender gender;
        private Dob dob;

        private UserStatus status;
        private boolean isEmailVerified;
        private Integer failedLoginAttemptCount;
        private Instant lockedUntil;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder roleId(Long roleId) {
            this.roleId = roleId;
            return this;
        }

        public Builder userSessionIds(List<Long> userSessionIds) {
            this.userSessionIds = userSessionIds;
            return this;
        }

        public Builder authProviderIds(List<Long> authProviderIds) {
            this.authProviderIds = authProviderIds;
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

        public Builder userSubscriptionIds(List<Long> userSubscriptionIds) {
            this.userSubscriptionIds = userSubscriptionIds;
            return this;
        }

        public Builder userDailyAiUsageIds(List<Long> userDailyAiUsageIds) {
            this.userDailyAiUsageIds = userDailyAiUsageIds;
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


        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public Builder isEmailVerified(boolean isEmailVerified) {
            this.isEmailVerified = isEmailVerified;
            return this;
        }

        public Builder failedLoginAttemptCount(Integer failedLoginAttemptCount) {
            this.failedLoginAttemptCount = failedLoginAttemptCount;
            return this;
        }

        public Builder lockedUntil(Instant lockedUntil) {
            this.lockedUntil = lockedUntil;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}