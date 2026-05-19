package org.naho.user.model;

import org.naho.file.model.File;
import org.naho.shared.exception.DomainException;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.type.Gender;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.UserStatus;
import org.naho.user.valueobject.Dob;
import org.naho.user.valueobject.Email;
import org.naho.user.valueobject.Password;
import org.naho.user.valueobject.Username;

import java.util.Set;

public class User {
    // identity
    private Long id;
    private Username username;
    private Email email;

    // authentication
    private Password password;
    private Set<Role> roles;

    // profile
    private String firstName;
    private String lastName;
    private Gender gender;
    private Dob dob; // data of birth

    private File avatar;

    private JLPTLevel jlptLevel;
    private UserStatus status;

    private User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
        this.password = builder.password;
        this.roles = builder.roles;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.gender = builder.gender;
        this.dob = builder.dob;
        this.avatar = builder.avatar;
        this.jlptLevel = builder.jlptLevel;
        this.status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Builder
    public static class Builder {
        private Long id;
        private Username username;
        private Email email;

        private Password password;
        private Set<Role> roles;

        private String firstName;
        private String lastName;
        private Gender gender;
        private Dob dob;
        private File avatar;
        private JLPTLevel jlptLevel;
        private UserStatus status = UserStatus.ACTIVE;

        public Builder id(Long id) {
            this.id = id;
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

        public Builder password(Password password) {
            this.password = password;
            return this;
        }

        public Builder roles(Set<Role> roles) {
            this.roles = roles;
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

        public Builder avatar(File avatar) {
            this.avatar = avatar;
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
            if (username == null) {
                throw new DomainException(
                        UserErrorCode.USERNAME_REQUIRED,
                        "Username is required!"
                );
            }

            if (email == null) {
                throw new DomainException(
                        UserErrorCode.EMAIL_REQUIRED,
                        "Email is required!"
                );
            }

            if (password == null) {
                throw new DomainException(
                        UserErrorCode.PASSWORD_REQUIRED,
                        "Password is required!"
                );
            }
            return new User(this);
        }
    }
}
