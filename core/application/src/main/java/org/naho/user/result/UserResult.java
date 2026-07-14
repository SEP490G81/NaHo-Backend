package org.naho.user.result;

import org.naho.file.result.FileResult;
import org.naho.point.result.PointSummaryResult;
import org.naho.user.type.Gender;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.UserStatus;

import java.time.LocalDate;
import java.util.List;

public record UserResult(
        Long id,
        List<RoleResult> roles,
        List<Long> userSessionIds,
        List<OAuthProviderResult> oAuthProviders,

        PointSummaryResult pointSummary,
        Long userLearningProgressId,
        FileResult avatar,

        String username,
        String email,
        String fullName,
        Gender gender,
        LocalDate dob,
        JLPTLevel jlptLevel,

        UserStatus status
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id;
        private List<RoleResult> roles;
        private List<Long> userSessionIds;
        private List<OAuthProviderResult> oAuthProviders;

        private PointSummaryResult pointSummary;
        private Long userLearningProgressId;
        private FileResult avatar;

        private String username;
        private String email;
        private String fullName;
        private Gender gender;
        private LocalDate dob;
        private JLPTLevel jlptLevel;

        private UserStatus status;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder roles(List<RoleResult> roles) {
            this.roles = roles;
            return this;
        }

        public Builder userSessionIds(List<Long> userSessionIds) {
            this.userSessionIds = userSessionIds;
            return this;
        }

        public Builder oAuthProviders(List<OAuthProviderResult> oAuthProviders) {
            this.oAuthProviders = oAuthProviders;
            return this;
        }

        public Builder pointSummary(PointSummaryResult pointSummary) {
            this.pointSummary = pointSummary;
            return this;
        }

        public Builder userLearningProgressId(Long userLearningProgressId) {
            this.userLearningProgressId = userLearningProgressId;
            return this;
        }

        public Builder avatar(FileResult avatar) {
            this.avatar = avatar;
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

        public Builder jlptLevel(JLPTLevel jlptLevel) {
            this.jlptLevel = jlptLevel;
            return this;
        }

        public Builder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public UserResult build() {
            return new UserResult(
                    id,
                    roles,
                    userSessionIds,
                    oAuthProviders,
                    pointSummary,
                    userLearningProgressId,
                    avatar,
                    username,
                    email,
                    fullName,
                    gender,
                    dob,
                    jlptLevel,
                    status
            );
        }
    }
}
