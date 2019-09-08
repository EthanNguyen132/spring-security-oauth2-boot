package com.ethan.oauth.config;

import java.security.KeyPair;
import java.security.Principal;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

@Configuration
@EnableAuthorizationServer
public class Oauth2Config extends AuthorizationServerConfigurerAdapter {

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(userRoleTokenEnhancer(), jwtAccessTokenConverter()));

		endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore())
				.tokenEnhancer(tokenEnhancerChain).reuseRefreshTokens(false);
		// don't reuse or we will run into session inactivity timeouts
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient("acme").secret(passwordEncoder.encode("acmesecret"))
				.authorizedGrantTypes("authorization_code", "refresh_token", "password").scopes("openid");
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()")
		.checkTokenAccess("permitAll()");
	}

	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "casablanca".toCharArray())
				.getKeyPair("jwt");
		converter.setKeyPair(keyPair);
		return converter;
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(jwtAccessTokenConverter());
	}

	@Bean
	public TokenEnhancer userRoleTokenEnhancer() {
		return new UserRoleTokenEnhancer();
	}

	public class UserRoleTokenEnhancer implements TokenEnhancer {
		@Override
		public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
			Map<String, Object> info = new HashMap<>();
			// assume one user has 1 role only
			if (authentication.getAuthorities().size() > 0) {
				info.put("role_name",
						authentication.getAuthorities().stream()
								.map(s -> s.getAuthority().split("_")[1] + "_PRIVILEGE")
								.collect(Collectors.toList()));
			}
			((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
			return accessToken;
		}

	}

	@FrameworkEndpoint
	class JwkSetEndpoint {
		KeyPair keyPair;
//
//		public JwkSetEndpoint(KeyPair keyPair) {
//			this.keyPair = keyPair;
//		}

		public JwkSetEndpoint() {
			keyPair = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "casablanca".toCharArray())
					.getKeyPair("jwt");
		}

		@GetMapping("/.well-known/jwks.json")
		@ResponseBody
		public Map<String, Object> getKey(Principal principal) {
			RSAPublicKey publicKey = (RSAPublicKey) this.keyPair.getPublic();
			RSAKey key = new RSAKey.Builder(publicKey).build();
			return new JWKSet(key).toJSONObject();
		}
	}

}
