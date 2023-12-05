package com.kosta.catdog.config;

import com.kosta.catdog.config.oauth2.OAuth2LoginSuccessHandler;
import com.kosta.catdog.config.oauth2.PrincipalOauth2UserService;
import com.kosta.catdog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.kosta.catdog.config.jwt.JwtAuthenticationFilter;
import com.kosta.catdog.config.jwt.JwtAuthorizationFilter;
import com.kosta.catdog.repository.UserDslRepository;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	@Autowired
	private CorsFilter corsFilter;

	@Autowired
	private CorsConfig corsConfig;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;

	@Autowired
	private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
			.addFilter(corsConfig.corsFilter())  // 다른 도메인 접근 허용
			.csrf().disable()  //csrf 공격 비활성화 
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);// session 비활성화
		// Login
		http
			.formLogin().disable()  // 로그인 폼 사용 비활성화 
			.httpBasic().disable()  // httpBasic은 header에 username, password를 암호화하지 않은 상태로 주고 받는다. 이를 사용하지 않겠다.
			.addFilter(new JwtAuthenticationFilter(authenticationManager())); // UsernamePasswordAuthenticationFilter

		//oauth2Login
		http
				.oauth2Login()
				.authorizationEndpoint().baseUri("/oauth2/authorization")  // 소셜 로그인 url
				.and()
				.redirectionEndpoint().baseUri("/oauth2/callback/*")  // 소셜 인증 후 redirect url
				.and()
				.userInfoEndpoint().userService(principalOauth2UserService)  // 회원 정보 처리
				.and()
				.successHandler(oAuth2LoginSuccessHandler);

		// 로그인 이후 권한처리
		http
				.addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository)) //BasicAuthenticationFilter
				.authorizeRequests()
				.antMatchers("/user/**").access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
				.antMatchers("/manager/**").access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
				.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')") // 로그인 & 권한
				.anyRequest().permitAll(); // 나머지는 허용
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();

	}



}