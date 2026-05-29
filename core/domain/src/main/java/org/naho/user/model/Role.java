package org.naho.user.model;

import org.naho.user.type.RoleName;

import java.util.Set;

public class Role {
    private Long id;
    private Set<Long> permissionIds;
    private Set<Long> userIds;

    private RoleName roleName;
    private String description;

    private Role() {
    }

    // Private constructor
    private Role(Builder builder) {
        this.id = builder.id;
        this.roleName = builder.roleName;
        this.description = builder.description;
        this.permissionIds = builder.permissionIds;
        this.userIds = builder.userIds;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public RoleName getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }

    public Set<Long> getPermissionIds() {
        return permissionIds;
    }

    public Set<Long> getUserIds() {
        return userIds;
    }

    // Builder Pattern
    public static class Builder {
        private Long id;
        private RoleName roleName;
        private String description;
        private Set<Long> permissionIds;
        private Set<Long> userIds;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder roleName(RoleName roleName) {
            this.roleName = roleName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder permissionIds(Set<Long> permissionIds) {
            this.permissionIds = permissionIds;
            return this;
        }

        public Builder userIds(Set<Long> userIds) {
            this.userIds = userIds;
            return this;
        }

        public Role build() {
            return new Role(this);
        }
    }
}