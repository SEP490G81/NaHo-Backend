package org.naho.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;
import org.naho.user.type.AuthProviderName;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auth_providers")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthProviderEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @Column(name = "provider_user_id", nullable = false)
    String providerUserId;

    @Column(name = "provider_name", nullable = false)
    AuthProviderName providerName;

    @Column(name = "avatar_url")
    String avatarUrl;
}
