package org.naho.user.mapper;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.file.result.FileResult;
import org.naho.point.port.in.CrudPointSummaryInputPort;
import org.naho.point.result.PointSummaryResult;
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
    private final CrudPointSummaryInputPort crudPointSummaryInputPort;
    private final CrudFileInputPort crudFileInputPort;

    public UserResultMapper(
            CrudRoleInputPort crudRoleInputPort,
            CrudOAuthProviderInputPort crudOAuthProviderInputPort,
            CrudPointSummaryInputPort crudPointSummaryInputPort,
            CrudFileInputPort crudFileInputPort
    ) {
        this.crudRoleInputPort = crudRoleInputPort;
        this.crudOAuthProviderInputPort = crudOAuthProviderInputPort;
        this.crudPointSummaryInputPort = crudPointSummaryInputPort;
        this.crudFileInputPort = crudFileInputPort;
    }

    public UserResult domainToResult(User domain) {
        LocalDate dob = domain.getDob() == null ? null : domain.getDob().getValue();

        List<RoleResult> roleResultList =
                crudRoleInputPort.findAllByUserId(domain.getId());
        List<OAuthProviderResult> oAuthProviderResultList =
                crudOAuthProviderInputPort.findAllByUser_Id(domain.getId());
        PointSummaryResult pointSummaryResult =
                crudPointSummaryInputPort.findPointSummaryByUserId(domain.getId());
        FileResult fileResult = domain.getAvatarFileId() == null ? null :
                crudFileInputPort.findById(domain.getAvatarFileId());

        return UserResult.builder()
                .id(domain.getId())
                .roles(roleResultList)
                .oAuthProviders(oAuthProviderResultList)
                .pointSummary(pointSummaryResult)
                .userLearningProgressId(domain.getUserLearningProgressId())
                .avatar(fileResult)
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
