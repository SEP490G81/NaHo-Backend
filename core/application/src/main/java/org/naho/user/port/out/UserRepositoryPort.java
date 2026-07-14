package org.naho.user.port.out;

import org.naho.user.model.OAuthProvider;
import org.naho.user.model.User;
import org.naho.user.type.OAuthProviderName;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderUserIdAndProviderName(String providerUserId, OAuthProviderName providerName);

    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User save(User user, OAuthProvider oAuthProvider);

    User createNew(User user, OAuthProvider oAuthProvider);
    
    Optional<User> findById(Long id);

    List<User> getListUser();

    List<User> findByFilters(String userNameOrMail, String role, String status, String jlptLevel);
}
