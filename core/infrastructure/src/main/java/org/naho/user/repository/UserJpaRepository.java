package org.naho.user.repository;

import jakarta.persistence.LockModeType;
import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.user.entity.UserEntity;
import org.naho.user.type.RoleName;
import org.naho.user.type.UserStatus;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends BaseJpaRepository<UserEntity> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM UserEntity u WHERE u.id = :userId")
    Optional<UserEntity> findByIdForUpdate(@Param("userId") Long userId);

    @Query("SELECT DISTINCT u FROM UserEntity u " +
            "LEFT JOIN u.role r " +
            "WHERE (:userNameOrEmail IS NULL OR :userNameOrEmail = '' " +
            "       OR LOWER(u.username) LIKE :userNameOrEmail " +
            "       OR LOWER(u.email) LIKE :userNameOrEmail) " +
            "AND (:role IS NULL OR r.roleName = :role) " +
            "AND (:status IS NULL OR u.status = :status)")
    List<UserEntity> findByFilters(
            @Param("userNameOrEmail") String userNameOrEmail,
            @Param("role") RoleName role,
            @Param("status") UserStatus status);
}
