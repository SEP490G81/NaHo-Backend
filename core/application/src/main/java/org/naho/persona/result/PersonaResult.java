package org.naho.persona.result;

import org.naho.file.result.FileResult;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.persona.type.PersonaStatus;
import org.naho.user.type.Gender;

public record PersonaResult(
        Long id,
        String name,
        String prompt,
        FileResult avatarFile,
        MarugotoLevel defaultMarugotoLevel,
        FormalityLevel defaultFormalityLevel,
        PersonaStatus status,
        String voiceName,
        Gender gender
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private String name;
        private String prompt;
        private FileResult avatarFile;
        private MarugotoLevel defaultMarugotoLevel;
        private FormalityLevel defaultFormalityLevel;
        private PersonaStatus status;
        private String voiceName;
        private Gender gender;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder avatarFile(FileResult avatarFile) {
            this.avatarFile = avatarFile;
            return this;
        }

        public Builder defaultMarugotoLevel(MarugotoLevel defaultMarugotoLevel) {
            this.defaultMarugotoLevel = defaultMarugotoLevel;
            return this;
        }

        public Builder defaultFormalityLevel(FormalityLevel defaultFormalityLevel) {
            this.defaultFormalityLevel = defaultFormalityLevel;
            return this;
        }

        public Builder status(PersonaStatus status) {
            this.status = status;
            return this;
        }

        public Builder voiceName(String voiceName) {
            this.voiceName = voiceName;
            return this;
        }

        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public PersonaResult build() {
            return new PersonaResult(
                    id,
                    name,
                    prompt,
                    avatarFile,
                    defaultMarugotoLevel,
                    defaultFormalityLevel,
                    status,
                    voiceName,
                    gender
            );
        }
    }
}
