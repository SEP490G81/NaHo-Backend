package org.naho.user.model;

import java.util.Set;

public class Permission {
    private Long id;
    private String code;
    private String description;
    private Set<Role> roles;

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Set<Role> getRoles() {
        return roles;
    }
}
