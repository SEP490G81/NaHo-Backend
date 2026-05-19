package org.naho.user.model;

import org.naho.user.type.PermissionCode;

import java.util.Set;

public class Permission {
    private Long id;
    private PermissionCode permissionCode;
    private String description;
    private Set<Role> roles;

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
}
