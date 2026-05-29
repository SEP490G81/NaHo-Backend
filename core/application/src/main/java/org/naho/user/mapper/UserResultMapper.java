package org.naho.user.mapper;

import org.naho.file.mapper.FileResultMapper;
import org.naho.file.result.FileResult;
import org.naho.user.model.User;
import org.naho.user.result.UserResult;

import java.util.List;

public class UserResultMapper {
    private final FileResultMapper fileResultMapper;

    public UserResultMapper(FileResultMapper fileResultMapper) {
        this.fileResultMapper = fileResultMapper;
    }

    public UserResult domainToResult(User domain) {
        if (domain == null) {
            return null;
        }

        List<String> roles = domain.getRoles()
                .stream().map(role -> role.getRoleName().toString())
                .toList();

        FileResult fileResult = fileResultMapper.domainToResult(domain.getAvatarFile());

        return UserResult.builder()
                .id(domain.getId())
                .username(domain.getUsername() != null ? domain.getUsername().getValue() : null)
                .email(domain.getEmail() != null ? domain.getEmail().getValue() : null)
                .roles(roles)
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .gender(domain.getGender())
                .dob(domain.getDob() != null ? domain.getDob().getValue() : null)
                .avatarFile(fileResult)
                .jlptLevel(domain.getJlptLevel())
                .status(domain.getStatus())
                .build();
    }
    public List<UserResult> domainsToResults(List<User> domains) {
        return domains.stream().map(this::domainToResult).toList();
    }
}
