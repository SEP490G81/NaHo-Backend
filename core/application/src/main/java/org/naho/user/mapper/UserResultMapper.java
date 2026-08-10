package org.naho.user.mapper;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.user.model.User;
import org.naho.user.port.in.CrudAuthProviderInputPort;
import org.naho.user.port.in.CrudRoleInputPort;
import org.naho.user.result.AuthProviderResult;
import org.naho.user.result.RoleResult;
import org.naho.user.result.UserResult;

import java.time.LocalDate;
import java.util.List;

public class UserResultMapper {

    private final CrudRoleInputPort crudRoleInputPort;
    private final CrudAuthProviderInputPort crudAuthProviderInputPort;
    private final CrudFileInputPort crudFileInputPort;

    public UserResultMapper(
            CrudRoleInputPort crudRoleInputPort,
            CrudAuthProviderInputPort crudAuthProviderInputPort,
            CrudFileInputPort crudFileInputPort
    ) {
        this.crudRoleInputPort = crudRoleInputPort;
        this.crudAuthProviderInputPort = crudAuthProviderInputPort;
        this.crudFileInputPort = crudFileInputPort;
    }

    public UserResult domainToResult(User domain) {
        LocalDate dob = domain.getDob() == null ? null : domain.getDob().getValue();

        RoleResult roleResult = domain.getRoleId() == null ? null :
                crudRoleInputPort.findRoleById(domain.getRoleId());
        List<AuthProviderResult> authProviderResultList =
                crudAuthProviderInputPort.findAllByUser_Id(domain.getId());
        FileResult fileResult = domain.getAvatarFileId() == null ? null :
                crudFileInputPort.findById(domain.getAvatarFileId());

        return UserResult.builder()
                .id(domain.getId())
                .role(roleResult)
                .authProviders(authProviderResultList)
                .userLearningProgressId(domain.getUserLearningProgressId())
                .avatarFile(fileResult)
                .username(domain.getUsername() != null ? domain.getUsername().getValue() : null)
                .email(domain.getEmail().getValue())
                .fullName(domain.getFullName())
                .gender(domain.getGender())
                .dob(dob)
                .status(domain.getStatus())
                .build();
    }
}
