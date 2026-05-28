package org.naho.user.model;

import org.naho.user.type.PermissionCode;

import java.util.Set;

public class Permission {
    private Long id;
    private Set<Long> roleIds;
    
    private PermissionCode permissionCode;
    private String description;

    // Private constructor
    private Permission(Builder builder) {
        this.id = builder.id;
        this.permissionCode = builder.permissionCode;
        this.description = builder.description;
        this.roleIds = builder.roleIds;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public PermissionCode getPermissionCode() {
        return permissionCode;
    }

    public String getDescription() {
        return description;
    }

    public Set<Long> getLongs() {
        return roleIds;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Builder class
    public static class Builder {
        private Long id;
        private PermissionCode permissionCode;
        private String description;
        private Set<Long> roleIds;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder permissionCode(PermissionCode permissionCode) {
            this.permissionCode = permissionCode;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder roleIds(Set<Long> roleIds) {
            this.roleIds = roleIds;
            return this;
        }

        public Permission build() {
            return new Permission(this);
        }
    }
}