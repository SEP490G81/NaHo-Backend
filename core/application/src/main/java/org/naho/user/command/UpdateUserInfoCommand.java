package org.naho.user.command;

import org.naho.file.command.FileUploadCommand;
import org.naho.user.type.Gender;

import java.time.LocalDate;

public record UpdateUserInfoCommand(
        Long id,
        String username,
        FileUploadCommand avatarFile,
        String fullName,
        Gender gender,
        LocalDate dob
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private String username;
        private FileUploadCommand avatarFile;
        private String fullName;
        private Gender gender;
        private LocalDate dob;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder avatarFile(FileUploadCommand avatarFile) {
            this.avatarFile = avatarFile;
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

        public UpdateUserInfoCommand build() {
            return new UpdateUserInfoCommand(
                    id,
                    username,
                    avatarFile,
                    fullName,
                    gender,
                    dob
            );
        }
    }
}