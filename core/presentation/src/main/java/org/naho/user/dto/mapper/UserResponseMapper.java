package org.naho.user.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.naho.file.dto.mapper.FileResponseMapper;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.result.FileResult;
import org.naho.user.dto.response.LeaderboardUserResponse;
import org.naho.user.dto.response.UserResponse;
import org.naho.user.result.LeaderboardUserResult;
import org.naho.user.result.UserResult;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        uses = {
                AuthProviderResponseMapper.class,
                FileResponseMapper.class,
                RoleResponseMapper.class
        }
)
public abstract class UserResponseMapper {
    @Autowired
    private FileStorageServicePort fileStorageServicePort;

    @Mapping(
            target = "avatarUrl",
            source = "avatarFile",
            qualifiedByName = "getAvatarUrlFromAvatarFile"
    )
    public abstract UserResponse resultToResponse(UserResult result);

    @Mapping(
            target = "avatarUrl",
            source = "avatarObjectKey",
            qualifiedByName = "getAvatarUrlFromAvatarObjectKey"
    )
    public abstract LeaderboardUserResponse resultToResponse(LeaderboardUserResult result);

    @Named("getAvatarUrlFromAvatarFile")
    protected String getAvatarUrlFromAvatarFile(FileResult avatarFile) {
        if (avatarFile == null) {
            return null;
        }
        return avatarFile.getAccessUrl();
    }

    @Named("getAvatarUrlFromAvatarObjectKey")
    protected String getAvatarUrlFromAvatarObjectKey(String avatarObjectKey) {
        if (avatarObjectKey == null || avatarObjectKey.isBlank()) {
            return null;
        }
        return fileStorageServicePort.generatePresignedUrl(avatarObjectKey);
    }
}
