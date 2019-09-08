package com.ethan.oauth.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ethan.oauth.model.UserRoles;
import com.ethan.oauth.repository.UserRolesRepository;
import com.ethan.oauth.security.AuthenticatedUser;

/**
 * @author ethannguyen
 **/

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRolesRepository userRolesRepository;

//	
//	@Autowired
//	private SystemUserRepository systemUserRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<UserRoles> userRoles = userRolesRepository.findByUserUsername(username);

		if (userRoles != null && !userRoles.isEmpty()) {
			List<GrantedAuthority> authorities = userRoles.stream().map(userRole -> 
				new SimpleGrantedAuthority(userRole.getRoleName())
			).collect(Collectors.toList());
			String password = userRoles.get(0).getUser().getPassword();
			return new AuthenticatedUser(username, password, authorities);
		}

		throw new UsernameNotFoundException(username);

	}

}
