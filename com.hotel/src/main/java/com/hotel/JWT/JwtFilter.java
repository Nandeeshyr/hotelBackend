package com.hotel.JWT;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//Every incoming request (except login, signup, forgot-password) is intercepted by your JwtFilter.

@Component
public class JwtFilter extends OncePerRequestFilter {
	//Spring will auto-register this filter. OncePerRequestFilter ensures it's run once per request.
	@Autowired
	private JwtUtil jwtUtil; // Used to parse, extract, and validate JWTs.

	@Autowired
	private CustomerUserDetailsService service; //Helps retrieve user details for Spring Security.

	Claims claims = null; //Full payload of the JWT.
	private String userName = null; //Extracted subject (usually email) from the token.

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws ServletException, IOException {
		
		if (httpServletRequest.getServletPath().matches("/user/login | /user/forgotPassword | /user/signup")) { //Allows unauthenticated access to public endpoints.
			filterChain.doFilter(httpServletRequest, httpServletResponse);
		} else {
			String authorizationHeader = httpServletRequest.getHeader("Authorization"); //Extracts the JWT from the Authorization header.
			String token = null;
			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
				token = authorizationHeader.substring(7); //Skips "Bearer " prefix to isolate the token.
				userName = jwtUtil.extractUsername(token); //Extracts email/username and role from the token.
				claims = jwtUtil.extractAllClaims(token);

			}
			if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) { //Ensures no other authentication is already set.
				UserDetails userDetails = service.loadUserByUsername(userName); //Loads user again to validate the token.
				if (jwtUtil.validateToken(token, userDetails)) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); 
					//Spring Security will now treat this user as authenticated for the rest of the request lifecycle.

				}
			}
			filterChain.doFilter(httpServletRequest, httpServletResponse); //Continues request processing after successful authentication.
 
		}
		

	}
	//Used to check role-based access elsewhere in code.
	public boolean isAdmin(){
		return "admin".equalsIgnoreCase((String) claims.get("role"));
	}
	public boolean isUser(){
		return "user".equalsIgnoreCase((String) claims.get("role"));
	}
	public String getCurrentUser() {
		return userName;
	}
}
