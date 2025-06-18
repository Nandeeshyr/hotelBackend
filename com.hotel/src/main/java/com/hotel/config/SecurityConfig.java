//package com.hotel.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
////    protected void configure(HttpSecurity http) throws Exception {
////        http
////            .csrf().disable() // Disable CSRF for REST APIs
////            .authorizeRequests()
////            .requestMatchers("/user/signup").permitAll() // Allow public access to signup
////            .anyRequest().authenticated();
////    }
//	@Bean
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//	    http.csrf().disable() // Disable CSRF for REST APIs
//	        .authorizeHttpRequests()
//	        .requestMatchers("/user/signup").permitAll()
//	        .anyRequest().authenticated();
//	    
//	    return http.build();
//	}
//
//}
