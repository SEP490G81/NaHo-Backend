package org.naho.user.result;

import org.naho.user.type.Gender;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.UserStatus;

import java.time.LocalDate;
import java.util.List;

public class UserResult {

    private final Long id;
    private final String username;
    private final List<String> roleNames;
    private final String avatarObjectKey;

    private final String email;
    private final String firstName;
    private final String lastName;
    private final Gender gender;
    private final LocalDate dob;
    private final JLPTLevel jlptLevel;
    private final UserStatus status;

    private UserResult(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
        this.roleNames = builder.roleNames;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.gender = builder.gender;
        this.dob = builder.dob;
        this.avatarObjectKey = builder.avatarObjectKey;
        this.jlptLevel = builder.jlptLevel;
        this.status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoleNames() {
        return roleNames;
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

    public LocalDate getDob() {
        return dob;
    }

    public String getAvatarObjectKey() {
        return avatarObjectKey;
    }

    public JLPTLevel getJlptLevel() {
        return jlptLevel;
    }

    public UserStatus getStatus() {
        return status;
    }

    public static class Builder {
        private Long id;
        private String username;
        private String email;
        private List<String> roleNames;
        private String firstName;
        private String lastName;
        private Gender gender;
        private LocalDate dob;
        private String avatarObjectKey;
        private JLPTLevel jlptLevel;
        private UserStatus status;

        public Builder id(Long id) {
            this.id = id;
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

        public Builder roleNames(List<String> roleNames) {
            this.roleNames = roleNames;
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

        public Builder dob(LocalDate dob) {
            this.dob = dob;
            return this;
        }

        public Builder avatarObjectKey(String avatarObjectKey) {
            this.avatarObjectKey = avatarObjectKey;
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

        public UserResult build() {
            return new UserResult(this);
        }
    }
}