package com.ethan.oauth.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticatedUser implements UserDetails, Serializable {

	private static final long serialVersionUID = 3207130705234235252L;

	private Long id;
	private String username;
	private String password;
	private int status; // TODO, Enums

	private Collection<GrantedAuthority> authorities;

	public AuthenticatedUser() {
		id = 0L;
		username = "";
		password = "";
		status = 0;
	}

	public AuthenticatedUser(List<GrantedAuthority> authorities) {
		this();
		this.authorities = authorities;
	}

	public AuthenticatedUser(User user, List<GrantedAuthority> authorities) {
		this();
		this.authorities = authorities;
	}

	public AuthenticatedUser(String username, String password, List<GrantedAuthority> authorities) {
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}


	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setUserName(String userName) {
		this.username = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuthorities(Collection<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
