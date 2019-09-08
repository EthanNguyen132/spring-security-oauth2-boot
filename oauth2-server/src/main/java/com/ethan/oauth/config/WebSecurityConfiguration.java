package com.ethan.oauth.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ethan.oauth.security.JwtAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private AuthenticationManagerBuilder authenticationManagerBuilder;

	@Autowired
	private JwtAuthenticationEntryPoint authenticationEntryPoint;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

//	@PostConstruct
//	public void init() throws Exception {
//		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
//	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.csrf().disable()
//			.exceptionHandling().authenticationEntryPoint(
//					(request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)).and()
		.authorizeRequests()
		 	.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
		 	.antMatchers("/oauth/**").permitAll()
		 	.antMatchers("/.well-known/**").permitAll()
			//.antMatchers("/**").authenticated()
			.antMatchers("/**").permitAll()
		.and().httpBasic().and()
		.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
		// @formatter:on
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		// TODO change to BCryptPasswordEncoder & update data.sql
		return new MessageDigestPasswordEncoder("SHA-256");
	}

}
