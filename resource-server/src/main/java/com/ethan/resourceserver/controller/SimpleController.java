package com.ethan.resourceserver.controller;

import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleController {

	@GetMapping("/whoami")
	public JwtAuthenticationToken whoami(JwtAuthenticationToken jwtAuthentication) {
		return jwtAuthentication;
    }
	
	@GetMapping("/admin")
	public String admin() {
		return "Hello admin";
    }
	
	@GetMapping("/me")
	public Map<String, Object> whoami() {
		JwtAuthenticationToken auth = (JwtAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		return auth.getTokenAttributes();
//		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
//		Map<String, Object> decodedDetails = (Map<String, Object>) details.getDecodedDetails();
//		return decodedDetails;
	}
	
	@GetMapping("/test")
	public String test(@AuthenticationPrincipal(expression="name") String name) {
		return name;
    }



}