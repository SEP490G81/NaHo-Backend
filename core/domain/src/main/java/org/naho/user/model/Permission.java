package org.naho.user.model;

import org.naho.user.type.PermissionCode;

import java.util.Set;

public class Permission {
    private Long id;
    private PermissionCode permissionCode;
    private String description;
    private Set<Role> roles;

    // Private constructor
    private Permission(Builder builder) {
        this.id = builder.id;
        this.permissionCode = builder.permissionCode;
        this.description = builder.description;
        this.roles = builder.roles;
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

    public Set<Role> getRoles() {
        return roles;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Builder class
    public static class Builder {
        private Long id;
        private PermissionCode permissionCode;
        private String description;
        private Set<Role> roles;

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

        public Builder roles(Set<Role> roles) {
            this.roles = roles;
            return this;
        }

        public Permission build() {
            return new Permission(this);
        }
    }
}