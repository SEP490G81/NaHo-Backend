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
        List<String> roles = domain.getRoles()
                .stream().map(role -> role.getRoleName().toString())
                .toList();

        FileResult fileResult = fileResultMapper.domainToResult(domain.getAvatarFile());

        return UserResult.builder()
                .id(domain.getId())
                .email(domain.getEmail().getValue())
                .roles(roles)
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .gender(domain.getGender())
                .dob(domain.getDob().getValue())
                .avatarFile(fileResult)
                .jlptLevel(domain.getJlptLevel())
                .build();
    }
}
