package org.naho.user.model;

import org.naho.user.type.RoleName;

import java.util.Set;

public class Role {
    private Long id;
    private RoleName roleName;
    private String description;
    private Set<Permission> permissions;
    private Set<User> users;

    // Private constructor
    private Role(Builder builder) {
        this.id = builder.id;
        this.roleName = builder.roleName;
        this.description = builder.description;
        this.permissions = builder.permissions;
        this.users = builder.users;
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

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<User> getUsers() {
        return users;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Builder Pattern
    public static class Builder {
        private Long id;
        private RoleName roleName;
        private String description;
        private Set<Permission> permissions;
        private Set<User> users;

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

        public Builder permissions(Set<Permission> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder users(Set<User> users) {
            this.users = users;
            return this;
        }

        public Role build() {
            return new Role(this);
        }
    }
}