package org.naho.user.model;

import org.naho.user.type.Gender;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.UserStatus;
import org.naho.user.valueobject.Dob;
import org.naho.user.valueobject.Email;
import org.naho.user.valueobject.Username;

import java.time.LocalDate;
import java.util.List;

public class User {
    private final Long id;
    private final List<Long> roleIds;
    private final List<Long> userSessionIds;
    private final Username username;
    private final Email email;
    private final String hashPassword;
    private final String avatarUrl;
    private final String fullName;
    private final Gender gender;
    private final Dob dob;
    private final JLPTLevel jlptLevel;
    private final Integer currentStreak;
    private final Integer longestStreak;
    private final LocalDate lastPracticeDate;
    private Long pointSummaryId;
    private UserStatus status;
    private String providerId;

    // Private constructor
    private User(Builder builder) {
        this.id = builder.id;
        this.avatarUrl = builder.avatarUrl;
        this.roleIds = builder.roleIds;
        this.userSessionIds = builder.userSessionIds;
        this.pointSummaryId = builder.pointSummaryId;
        this.username = builder.username;
        this.email = builder.email;
        this.hashPassword = builder.hashPassword;
        this.fullName = builder.fullName;
        this.gender = builder.gender;
        this.dob = builder.dob;
        this.jlptLevel = builder.jlptLevel;
        this.status = builder.status;
        this.currentStreak = builder.currentStreak;
        this.longestStreak = builder.longestStreak;
        this.lastPracticeDate = builder.lastPracticeDate;
        this.providerId = builder.providerId;
    }

    // Static builder method
    public static Builder builder() {
        return new Builder();
    }

    public static User registerNewUser(String rawUsername, String hashPassword, String rawEmail, List<Long> roleIds) {
        return User.builder()
                .username(Username.of(rawUsername))
                .email(Email.of(rawEmail))
                .hashPassword(hashPassword)
                .status(UserStatus.ACTIVE)
                .roleIds(roleIds)
                .currentStreak(0)
                .longestStreak(0)
                .build();
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public List<Long> getUserSessionIds() {
        return userSessionIds;
    }

    public Long getPointSummaryId() {
        return pointSummaryId;
    }

    public void setPointSummaryId(Long pointSummaryId) {
        this.pointSummaryId = pointSummaryId;
    }

    public Username getUsername() {
        return username;
    }

    public Email getEmail() {
        return email;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public Gender getGender() {
        return gender;
    }

    public Dob getDob() {
        return dob;
    }

    public JLPTLevel getJlptLevel() {
        return jlptLevel;
    }

    public UserStatus getStatus() {
        return status;
    }

    // Setters
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public Integer getLongestStreak() {
        return longestStreak;
    }

    public LocalDate getLastPracticeDate() {
        return lastPracticeDate;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    // Builder class
    public static class Builder {

        private Long id;
        private List<Long> roleIds;
        private List<Long> userSessionIds;
        private Long pointSummaryId;

        private Username username;
        private Email email;

        private String hashPassword;

        private String avatarUrl;
        private String fullName;
        private Gender gender;
        private Dob dob;

        private JLPTLevel jlptLevel;
        private UserStatus status;
        private Integer currentStreak;
        private Integer longestStreak;
        private LocalDate lastPracticeDate;
        private String providerId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
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

        public Builder pointSummaryId(Long pointSummaryId) {
            this.pointSummaryId = pointSummaryId;
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

        public Builder currentStreak(Integer currentStreak) {
            this.currentStreak = currentStreak;
            return this;
        }

        public Builder longestStreak(Integer longestStreak) {
            this.longestStreak = longestStreak;
            return this;
        }

        public Builder lastPracticeDate(LocalDate lastPracticeDate) {
            this.lastPracticeDate = lastPracticeDate;
            return this;
        }

        public Builder providerId(String providerId) {
            this.providerId = providerId;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}