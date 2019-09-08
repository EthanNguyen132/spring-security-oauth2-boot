package com.ethan.resourceserver.config;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@EnableWebSecurity
public class ResourceServerConfiguration extends WebSecurityConfigurerAdapter {
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/admin/**").hasAuthority("ADMIN_PRIVILEGE")        
		        .antMatchers("/whoami/**", "/me/**").hasAuthority("USER_PRIVILEGE")                  
		        .anyRequest()
				.authenticated()
			.and()
			.oauth2ResourceServer()
			.jwt()
				.jwkSetUri("http://localhost:8080/.well-known/jwks.json")
				.jwtAuthenticationConverter(grantedAuthoritiesExtractor());
	}

	Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractor() {
		return new GrantedAuthoritiesExtractor();
	}

	static class GrantedAuthoritiesExtractor extends JwtAuthenticationConverter {
		protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
			Collection<String> authorities = (Collection<String>) jwt.getClaims().get("role_name");
			return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
		}
	}
}
