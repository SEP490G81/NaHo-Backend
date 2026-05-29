package org.naho.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.naho.user.type.DeviceType;
import org.naho.user.type.SessionRevokedReason;

import java.time.Instant;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_sessions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @Column(name = "hash_refresh_token", nullable = false, length = 512)
    String hashRefreshToken;

    @Column(name = "device_id", length = 100)
    String deviceId;

    @Column(name = "device_name")
    String deviceName;

    @Column(name = "device_type", length = 50)
    @Enumerated(EnumType.STRING)
    DeviceType deviceType;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    String userAgent;

    @Column(name = "ip_address", length = 45)
    String ipAddress;

    @Column(name = "issued_at", nullable = false)
    Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    Instant expiresAt;

    @Column(name = "last_used_at")
    Instant lastUsedAt;

    @Column(name = "revoked_at")
    Instant revokedAt;

    @Column(name = "revoked_reason", length = 50)
    @Enumerated(EnumType.STRING)
    SessionRevokedReason revokedReason;
}
