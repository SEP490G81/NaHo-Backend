package org.naho.user.mapper;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.user.model.User;
import org.naho.user.port.in.CrudOAuthProviderInputPort;
import org.naho.user.port.in.CrudRoleInputPort;
import org.naho.user.result.OAuthProviderResult;
import org.naho.user.result.RoleResult;
import org.naho.user.result.UserResult;

import java.time.LocalDate;
import java.util.List;

public class UserResultMapper {

    private final CrudRoleInputPort crudRoleInputPort;
    private final CrudOAuthProviderInputPort crudOAuthProviderInputPort;
    private final CrudFileInputPort crudFileInputPort;

    public UserResultMapper(
            CrudRoleInputPort crudRoleInputPort,
            CrudOAuthProviderInputPort crudOAuthProviderInputPort,
            CrudFileInputPort crudFileInputPort
    ) {
        this.crudRoleInputPort = crudRoleInputPort;
        this.crudOAuthProviderInputPort = crudOAuthProviderInputPort;
        this.crudFileInputPort = crudFileInputPort;
    }

    public UserResult domainToResult(User domain) {
        LocalDate dob = domain.getDob() == null ? null : domain.getDob().getValue();

        List<RoleResult> roleResultList =
                crudRoleInputPort.findAllByUserId(domain.getId());
        List<OAuthProviderResult> oAuthProviderResultList =
                crudOAuthProviderInputPort.findAllByUser_Id(domain.getId());
        FileResult fileResult = domain.getAvatarFileId() == null ? null :
                crudFileInputPort.findById(domain.getAvatarFileId());

        return UserResult.builder()
                .id(domain.getId())
                .roles(roleResultList)
                .oAuthProviders(oAuthProviderResultList)
                .userLearningProgressId(domain.getUserLearningProgressId())
                .avatarFile(fileResult)
                .username(domain.getUsername() != null ? domain.getUsername().getValue() : null)
                .email(domain.getEmail().getValue())
                .fullName(domain.getFullName())
                .gender(domain.getGender())
                .dob(dob)
                .jlptLevel(domain.getJlptLevel())
                .status(domain.getStatus())
                .build();
    }
}
