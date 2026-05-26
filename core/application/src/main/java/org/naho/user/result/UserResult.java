package org.naho.user.result;

import org.naho.file.result.FileResult;
import org.naho.user.type.Gender;
import org.naho.user.type.JLPTLevel;

import java.time.LocalDate;
import java.util.List;

public class UserResult {

    private final Long id;
    private final String email;
    private final List<String> roles;
    private final String firstName;
    private final String lastName;
    private final Gender gender;
    private final LocalDate dob;
    private final FileResult avatarFile;
    private final JLPTLevel jlptLevel;

    private UserResult(Builder builder) {
        this.id = builder.id;
        this.email = builder.email;
        this.roles = builder.roles;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.gender = builder.gender;
        this.dob = builder.dob;
        this.avatarFile = builder.avatarFile;
        this.jlptLevel = builder.jlptLevel;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
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

    public FileResult getAvatarFile() {
        return avatarFile;
    }

    public JLPTLevel getJlptLevel() {
        return jlptLevel;
    }

    public static class Builder {

        private Long id;
        private String email;
        private List<String> roles;
        private String firstName;
        private String lastName;
        private Gender gender;
        private LocalDate dob;
        private FileResult avatarFile;
        private JLPTLevel jlptLevel;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder roles(List<String> roles) {
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

        public Builder dob(LocalDate dob) {
            this.dob = dob;
            return this;
        }

        public Builder avatarFile(FileResult avatarFile) {
            this.avatarFile = avatarFile;
            return this;
        }

        public Builder jlptLevel(JLPTLevel jlptLevel) {
            this.jlptLevel = jlptLevel;
            return this;
        }

        public UserResult build() {
            return new UserResult(this);
        }
    }
}