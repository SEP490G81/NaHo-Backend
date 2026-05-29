package org.naho.user.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.user.entity.UserEntity;
import org.naho.user.type.JLPTLevel;
import org.naho.user.type.RoleName;
import org.naho.user.type.UserStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface UserJpaRepository extends BaseJpaRepository<UserEntity> {
    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsernameOrEmail(String username, String email);

    @Query("SELECT DISTINCT u FROM UserEntity u " +
           "LEFT JOIN u.roles r " +
           "WHERE (:userNameOrEmail IS NULL OR :userNameOrEmail = '' " +
           "       OR LOWER(u.username) LIKE :userNameOrEmail " +
           "       OR LOWER(u.email) LIKE :userNameOrEmail) " +
           "AND (:role IS NULL OR r.roleName = :role) " +
           "AND (:status IS NULL OR u.status = :status) " +
           "AND (:jlptLevel IS NULL OR u.jlptLevel = :jlptLevel)")
    List<UserEntity> findByFilters(
            @Param("userNameOrEmail") String userNameOrEmail,
            @Param("role") RoleName role,
            @Param("status") UserStatus status,
            @Param("jlptLevel") JLPTLevel jlptLevel);
}
