package org.naho.user.mapper;

import org.naho.user.model.User;
import org.naho.user.result.UserResult;

import java.time.LocalDate;
import java.util.List;

public class UserResultMapper {
    public UserResult domainToResult(User domain, List<String> roleNames, String avatarFileUrl) {
        LocalDate dob = domain.getDob() == null ? null : domain.getDob().getValue();

        return UserResult.builder()
                .id(domain.getId())
                .username(domain.getUsername() != null ? domain.getUsername().getValue() : null)
                .email(domain.getEmail().getValue())
                .roleNames(roleNames)
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .gender(domain.getGender())
                .dob(dob)
                .avatarFileUrl(avatarFileUrl)
                .jlptLevel(domain.getJlptLevel())
                .status(domain.getStatus())
                .build();
    }
}
