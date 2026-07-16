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

    private final Long pointSummaryId;
    private final Long userLearningProgressId;
    private Long avatarFileId;

    private final Username username;
    private final Email email;
    private String hashPassword;
    private String fullName;
    private Gender gender;
    private Dob dob;
    private JLPTLevel jlptLevel;

    private UserStatus status;

    private User(Builder builder) {
        this.id = builder.id;
        this.roleIds = builder.roleIds;
        this.userSessionIds = builder.userSessionIds;
        this.oAuthProviderIds = builder.oAuthProviderIds;
        this.pointSummaryId = builder.pointSummaryId;
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
    }

    public static User registerNewUser(String rawUsername, String hashPassword, String rawEmail, List<Long> roleIds) {
        return User.builder()
                .username(Username.of(rawUsername))
                .email(Email.of(rawEmail))
                .hashPassword(hashPassword)
                .status(UserStatus.ACTIVE)
                .roleIds(roleIds)
                .build();
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return builder()
                .id(id)
                .roleIds(roleIds)
                .userSessionIds(userSessionIds)
                .pointSummaryId(pointSummaryId)
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

    public static final class Builder {

        private Long id;
        private List<Long> roleIds;
        private List<Long> userSessionIds;
        private List<Long> oAuthProviderIds;

        private Long pointSummaryId;
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

        public Builder pointSummaryId(Long pointSummaryId) {
            this.pointSummaryId = pointSummaryId;
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

        public User build() {
            return new User(this);
        }
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

    public Long getPointSummaryId() {
        return pointSummaryId;
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
}