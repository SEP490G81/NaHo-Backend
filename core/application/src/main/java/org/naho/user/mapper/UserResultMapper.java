package org.naho.user.mapper;

import org.naho.user.model.User;
import org.naho.user.result.UserResult;

import java.time.LocalDate;
import java.util.List;

public class UserResultMapper {
    public UserResult domainToResult(User domain, List<String> roleNames) {
        LocalDate dob = domain.getDob() == null ? null : domain.getDob().getValue();

        return UserResult.builder()
                .id(domain.getId())
                .username(domain.getUsername() != null ? domain.getUsername().getValue() : null)
                .email(domain.getEmail().getValue())
                .roleNames(roleNames)
                .fullName(domain.getFullName())
                .gender(domain.getGender())
                .dob(dob)
                .avatarUrl(domain.getAvatarUrl())
                .jlptLevel(domain.getJlptLevel())
                .status(domain.getStatus())
                .userLearningProgressId(domain.getUserLearningProgressId())
                .build();
    }
}
