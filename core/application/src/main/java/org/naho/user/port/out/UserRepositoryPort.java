package org.naho.user.port.out;

import org.naho.file.model.File;
import org.naho.user.command.UpdateUserInfoCommand;
import org.naho.user.model.AuthProvider;
import org.naho.user.model.User;
import org.naho.user.result.LeaderboardUserResult;
import org.naho.user.type.AuthProviderName;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderUserIdAndProviderName(String providerUserId, AuthProviderName providerName);

    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User save(User user);

    User createNew(User user, AuthProvider authProvider);

    Optional<User> findById(Long id);

    List<User> getListUser();

    List<User> findByFilters(String userNameOrMail, String role, String status);

    List<LeaderboardUserResult> findTop10OrderByTotalPointInLeague(Long leagueId);

    Optional<LeaderboardUserResult> findTopOfUserByUserId(Long userId);

    void lockById(Long userId);

    User updateUserInfo(UpdateUserInfoCommand command);

    User updateUserAvatar(Long userId, File newAvatarFile);
}
