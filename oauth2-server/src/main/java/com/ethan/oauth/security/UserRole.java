package com.ethan.oauth.security;

public enum UserRole {
	ROLE_USER("ROLE_USER"), ROLE_ADMIN("ROLE_ADMIN");

	UserRole(String roleName) {
	}

	public static UserRole valueOfIgnoreCase(String roleName) {
		roleName = roleName.toUpperCase();
		return valueOf(roleName);
	}
}
