package org.naho.user.result;

import org.naho.file.result.FileResult;
import org.naho.user.type.Gender;
import org.naho.user.type.UserStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class UserResult {

    private Long id;
    private RoleResult role;
    private List<AuthProviderResult> authProviders;
    private FileResult avatarFile;
    private Long userLearningProgressId;

    private String username;
    private String email;
    private String fullName;
    private Gender gender;
    private LocalDate dob;
    private UserStatus status;
    private boolean isEmailVerified;

    public UserResult() {
    }

    public UserResult(
            Long id,
            RoleResult role,
            List<AuthProviderResult> authProviders,
            FileResult avatarFile,
            Long userLearningProgressId,
            String username,
            String email,
            String fullName,
            Gender gender,
            LocalDate dob,
            UserStatus status
    ) {
        this(id, role, authProviders, avatarFile, userLearningProgressId, username, email, fullName, gender, dob, status, false);
    }

    public UserResult(
            Long id,
            RoleResult role,
            List<AuthProviderResult> authProviders,
            FileResult avatarFile,
            Long userLearningProgressId,
            String username,
            String email,
            String fullName,
            Gender gender,
            LocalDate dob,
            UserStatus status,
            boolean isEmailVerified
    ) {
        this.id = id;
        this.role = role;
        this.authProviders = authProviders;
        this.avatarFile = avatarFile;
        this.userLearningProgressId = userLearningProgressId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
        this.dob = dob;
        this.status = status;
        this.isEmailVerified = isEmailVerified;
    }

    private UserResult(Builder builder) {
        this.id = builder.id;
        this.role = builder.role;
        this.authProviders = builder.authProviders;
        this.avatarFile = builder.avatarFile;
        this.userLearningProgressId = builder.userLearningProgressId;
        this.username = builder.username;
        this.email = builder.email;
        this.fullName = builder.fullName;
        this.gender = builder.gender;
        this.dob = builder.dob;
        this.status = builder.status;
        this.isEmailVerified = builder.isEmailVerified;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RoleResult getRole() {
        return role;
    }

    public void setRole(RoleResult role) {
        this.role = role;
    }

    public List<AuthProviderResult> getAuthProviders() {
        return authProviders;
    }

    public void setAuthProviders(List<AuthProviderResult> authProviders) {
        this.authProviders = authProviders;
    }

    public FileResult getAvatarFile() {
        return avatarFile;
    }

    public void setAvatarFile(FileResult avatarFile) {
        this.avatarFile = avatarFile;
    }

    public Long getUserLearningProgressId() {
        return userLearningProgressId;
    }

    public void setUserLearningProgressId(Long userLearningProgressId) {
        this.userLearningProgressId = userLearningProgressId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    // Record-style accessors for backward compatibility
    public Long id() {
        return id;
    }

    public RoleResult role() {
        return role;
    }

    public List<AuthProviderResult> authProviders() {
        return authProviders;
    }

    public FileResult avatarFile() {
        return avatarFile;
    }

    public Long userLearningProgressId() {
        return userLearningProgressId;
    }

    public String username() {
        return username;
    }

    public String email() {
        return email;
    }

    public String fullName() {
        return fullName;
    }

    public Gender gender() {
        return gender;
    }

    public LocalDate dob() {
        return dob;
    }

    public UserStatus status() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResult that = (UserResult) o;
        return isEmailVerified == that.isEmailVerified &&
                Objects.equals(id, that.id) &&
                Objects.equals(role, that.role) &&
                Objects.equals(authProviders, that.authProviders) &&
                Objects.equals(avatarFile, that.avatarFile) &&
                Objects.equals(userLearningProgressId, that.userLearningProgressId) &&
                Objects.equals(username, that.username) &&
                Objects.equals(email, that.email) &&
                Objects.equals(fullName, that.fullName) &&
                gender == that.gender &&
                Objects.equals(dob, that.dob) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, authProviders, avatarFile, userLearningProgressId, username, email, fullName, gender, dob, status, isEmailVerified);
    }

    @Override
    public String toString() {
        return "UserResult{" +
                "id=" + id +
                ", role=" + role +
                ", authProviders=" + authProviders +
                ", avatarFile=" + avatarFile +
                ", userLearningProgressId=" + userLearningProgressId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", gender=" + gender +
                ", dob=" + dob +
                ", status=" + status +
                ", isEmailVerified=" + isEmailVerified +
                '}';
    }

    public static final class Builder {

        private Long id;
        private RoleResult role;
        private List<AuthProviderResult> authProviders;

        private Long userLearningProgressId;
        private FileResult avatarFile;

        private String username;
        private String email;
        private String fullName;
        private Gender gender;
        private LocalDate dob;

        private UserStatus status;
        private boolean isEmailVerified;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder role(RoleResult role) {
            this.role = role;
            return this;
        }

        public Builder authProviders(List<AuthProviderResult> authProviders) {
            this.authProviders = authProviders;
            return this;
        }

        public Builder userLearningProgressId(Long userLearningProgressId) {
            this.userLearningProgressId = userLearningProgressId;
            return this;
        }

        public Builder avatarFile(FileResult avatarFile) {
            this.avatarFile = avatarFile;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
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

        public Builder dob(LocalDate dob) {
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

        public UserResult build() {
            return new UserResult(this);
        }
    }
}
