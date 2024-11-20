package com.cpuoverload.intelliedu.model.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("user"),
    ADMIN("admin"),
    VIP("vip");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

}
