package org.naho.user.model;

import org.naho.user.type.AccountType;
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
    private final Long avatarFileId;
    private final List<Long> roleIds;
    private final List<Long> userSessionIds;

    private final Username username;
    private final Email email;

    private final String hashPassword;
    private final AccountType accountType;

    private final String firstName;
    private final String lastName;
    private final Gender gender;
    private final Dob dob;

    private final JLPTLevel jlptLevel;
    private final UserStatus status;
    private final Integer currentStreak;
    private final Integer longestStreak;
    private final LocalDate lastPracticeDate;

    // Private constructor
    private User(Builder builder) {
        this.id = builder.id;
        this.avatarFileId = builder.avatarFileId;
        this.roleIds = builder.roleIds;
        this.userSessionIds = builder.userSessionIds;
        this.username = builder.username;
        this.email = builder.email;
        this.hashPassword = builder.hashPassword;
        this.accountType = builder.accountType;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.gender = builder.gender;
        this.dob = builder.dob;
        this.jlptLevel = builder.jlptLevel;
        this.status = builder.status;
        this.currentStreak = builder.currentStreak;
        this.longestStreak = builder.longestStreak;
        this.lastPracticeDate = builder.lastPracticeDate;
    }

    // Static builder method
    public static Builder builder() {
        return new Builder();
    }

    // Build Register User
    public static User registerNewUser(String rawUsername, String hashPassword, String rawEmail, List<Long> roleIds) {
        return User.builder()
                .username(Username.of(rawUsername))
                .email(Email.of(rawEmail))
                .hashPassword(hashPassword)
                .accountType(AccountType.CREDENTIALS)
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

    public Long getAvatarFileId() {
        return avatarFileId;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public List<Long> getUserSessionIds() {
        return userSessionIds;
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

    public AccountType getAccountType() {
        return accountType;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public Integer getLongestStreak() {
        return longestStreak;
    }

    public LocalDate getLastPracticeDate() {
        return lastPracticeDate;
    }

    // Builder class
    public static class Builder {

        private Long id;
        private Long avatarFileId;
        private List<Long> roleIds;
        private List<Long> userSessionIds;

        private Username username;
        private Email email;

        private String hashPassword;
        private AccountType accountType;

        private String firstName;
        private String lastName;
        private Gender gender;
        private Dob dob;

        private JLPTLevel jlptLevel;
        private UserStatus status;
        private Integer currentStreak;
        private Integer longestStreak;
        private LocalDate lastPracticeDate;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder avatarFileId(Long avatarFileId) {
            this.avatarFileId = avatarFileId;
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

        public Builder accountType(AccountType accountType) {
            this.accountType = accountType;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
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

        public User build() {
            return new User(this);
        }
    }
}