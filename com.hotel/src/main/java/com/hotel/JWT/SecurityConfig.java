package com.hotel.JWT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity //Registers this class as a config and turns on web security.
public class SecurityConfig {

	@Autowired
	private CustomerUserDetailsService customerUserDetailsService;

	@Autowired
	JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { //Defines your custom security rules.
		http.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()))
				.authorizeHttpRequests(
						auth -> auth.requestMatchers("/user/login", "/user/signup", "/user/forgotPassword").permitAll() //Public endpoints—no token required.
								.anyRequest().authenticated())															//All other endpoints need a valid JWT.
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))	//Each request must carry its own token—no sessions or cookies.
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);						//Ensures your JWT filter runs before the default login process.

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() { //Tells Spring Security to use our custom UserDetailsService and BCryptPasswordEncoder for authentication.
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(customerUserDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
}
