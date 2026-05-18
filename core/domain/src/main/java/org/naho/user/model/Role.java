package org.naho.user.model;

import org.naho.user.type.RoleName;

import java.util.Set;

public class Role {
    private Long id;
    private RoleName roleName;
    private String description;
    private Set<Permission> permissions;
    private Set<User> users;

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
}
