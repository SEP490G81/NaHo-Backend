package org.naho.user.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.entity.FileEntity;
import org.naho.file.model.File;
import org.naho.file.repository.FileJpaRepository;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.pagination.PageData;
import org.naho.pagination.PageMeta;
import org.naho.shared.exception.InfrastructureException;
import org.naho.user.command.UpdateUserInfoCommand;
import org.naho.user.command.UserQueryCommand;
import org.naho.user.entity.AuthProviderEntity;
import org.naho.user.entity.UserEntity;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.AuthProviderEntityMapper;
import org.naho.user.mapper.UserEntityMapper;
import org.naho.user.model.AuthProvider;
import org.naho.user.model.User;
import org.naho.user.mybatis.UserQueryMapper;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.repository.UserJpaRepository;
import org.naho.user.result.LeaderboardUserResult;
import org.naho.user.specification.UserSpecification;
import org.naho.user.type.AuthProviderName;
import org.naho.user.type.Gender;
import org.naho.user.type.RoleName;
import org.naho.user.type.UserStatus;
import org.naho.user.valueobject.Dob;
import org.naho.user.valueobject.Username;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userEntityMapper;
    private final UserQueryMapper userQueryMapper;
    private final AuthProviderEntityMapper authProviderEntityMapper;
    private final FileJpaRepository fileJpaRepository;

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userEntityMapper::entityToDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userEntityMapper::entityToDomain);
    }

    @Override
    public Optional<User> findByProviderUserIdAndProviderName(String providerId, AuthProviderName providerName) {
        return userQueryMapper.findByProviderUserIdAndProviderName(providerId, providerName)
                .map(userEntityMapper::entityToDomain);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userJpaRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(userEntityMapper::entityToDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userEntityMapper.domainToEntity(user);
        UserEntity savedEntity = userJpaRepository.save(userEntity);
        return userEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public User createNew(User user, AuthProvider authProvider) {
        UserEntity userEntity = userEntityMapper.domainToEntity(user);

        if (authProvider != null) {
            AuthProviderEntity authProviderEntity = authProviderEntityMapper.domainToEntity(authProvider);
            authProviderEntity.setUser(userEntity);
            userEntity.getAuthProviders().add(authProviderEntity);
        }

        UserEntity savedEntity = userJpaRepository.save(userEntity);
        return userEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public List<User> findByFilters(String userNameOrMail, String role, String status) {
        RoleName roleEnum = null;
        if (role != null && !role.trim().isEmpty()) {
            try {
                roleEnum = RoleName.valueOf(role.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }

        UserStatus statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = UserStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }

        String formattedNameOrMail = null;
        if (userNameOrMail != null && !userNameOrMail.trim().isEmpty()) {
            formattedNameOrMail = "%" + userNameOrMail.trim().toLowerCase() + "%";
        }

        return userJpaRepository.findByFilters(formattedNameOrMail, roleEnum, statusEnum).stream()
                .map(userEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public List<LeaderboardUserResult> findTop10OrderByTotalPointInLeague(Long leagueId) {
        return userQueryMapper.findTop10OrderByTotalPointInLeague(leagueId);
    }

    @Override
    public Optional<LeaderboardUserResult> findTopOfUserByUserId(Long userId) {
        return userQueryMapper.findTopOfUserByUserId(userId);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository
                .findById(id)
                .map(userEntityMapper::entityToDomain);
    }

    @Override
    public void lockById(Long userId) {
        userJpaRepository.findByIdForUpdate(userId);
    }

    @Override
    public User updateUserInfo(UpdateUserInfoCommand command) {
        UserEntity user = userJpaRepository.findById(command.id())
                .orElseThrow(() -> new InfrastructureException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        command.id()
                ));

        String commandUsername = command.username();
        if (commandUsername != null && !commandUsername.isBlank()) {
            if (
                    !commandUsername.equals(user.getUsername()) &&
                            userJpaRepository.existsByUsername(commandUsername)
            ) {
                throw new InfrastructureException(
                        UserErrorCode.USER_ALREADY_EXISTS,
                        UserDetailMessageKey.USER_USERNAME_ALREADY_EXISTS,
                        commandUsername
                );
            }
            user.setUsername(Username.of(commandUsername).getValue());
        }

        String commandFullName = command.fullName();
        if (commandFullName != null && !commandFullName.isBlank()) {
            user.setFullName(commandFullName);
        }

        Gender commandGender = command.gender();
        if (commandGender != null) {
            user.setGender(commandGender);
        }

        LocalDate commandDob = command.dob();
        if (commandDob != null) {
            user.setDob(Dob.of(commandDob).getValue());
        }

        UserEntity savedEntity = userJpaRepository.save(user);
        return userEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public User updateUserAvatar(Long userId, File newAvatarFile) {
        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new InfrastructureException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        userId
                ));

        FileEntity fileEntity = fileJpaRepository.getReferenceById(newAvatarFile.getId());
        user.setAvatarFile(fileEntity);

        UserEntity savedUser = userJpaRepository.save(user);

        return userEntityMapper.entityToDomain(savedUser);
    }

    /**
     * Method để query xuống db và lấy ra danh sách người dùng kèm các điều kiện
     *
     * @param command chứa các điều kiện search, filter, sort
     * @return PageData<User>
     */
    @Override
    public PageData<User> findAllUsers(UserQueryCommand command) {
        // tạo đối tượng Pageable gồm:
        // số trang, số element trên 1 trang, sort column, sort direction
        Pageable pageable = PageRequest.of(
                command.page(),
                command.size(),
                Sort.by(
                        Sort.Direction.valueOf(command.sortDirection().name()),
                        command.sortColumn().getColumnName()
                )
        );

        // gom các điều kiện của searchKeyword
        Specification<UserEntity> searchKeywordSpecification = Specification.anyOf(
                UserSpecification.hasEmail(command.searchKeyword()),
                UserSpecification.hasUsername(command.searchKeyword()),
                UserSpecification.hasFullName(command.searchKeyword())
        );

        // điều kiện cuối cùng
        Specification<UserEntity> specification = Specification.allOf(
                searchKeywordSpecification,
                UserSpecification.hasGender(command.gender()),
                UserSpecification.dobBetween(command.dobFrom(), command.dobTo()),
                UserSpecification.hasStatus(command.status()),
                UserSpecification.hasRoleId(command.roleId()),
                UserSpecification.isEmailVerified(command.isEmailVerified()),
                UserSpecification.notAdminRole()
        );

        Page<UserEntity> page = userJpaRepository.findAll(specification, pageable);

        return PageData.<User>builder()
                .pageMeta(PageMeta.builder()
                        .currentPage(page.getNumber())
                        .pageSize(page.getSize())
                        .totalPages(page.getTotalPages())
                        .totalElements(page.getTotalElements())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .build())
                .data(page.getContent()
                        .stream()
                        .map(userEntityMapper::entityToDomain)
                        .toList()
                )
                .build();
    }

    @Override
    public User updateUserStatus(Long id, UserStatus newStatus) {
        UserEntity user = userJpaRepository.findById(id)
                .orElseThrow(() -> new InfrastructureException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        id
                ));

        user.setStatus(newStatus);

        UserEntity savedUser = userJpaRepository.save(user);
        return userEntityMapper.entityToDomain(savedUser);
    }
}
