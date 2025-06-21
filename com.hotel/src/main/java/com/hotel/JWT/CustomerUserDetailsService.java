package com.hotel.JWT;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hotel.dao.UserDao;

import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Enables Lombok-based logging via SLF4J
@Service // Tells Spring that this is a service bean
public class CustomerUserDetailsService implements UserDetailsService {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CustomerUserDetailsService.class);

	@Autowired // Injects your DAO to access user data. Used to fetch user data from the
				// database.
	UserDao userDao;

	private com.hotel.POJO.User userDetail; // Temporarily stores the full user object for later use (e.g., in UserServiceImpl).

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Called by Spring Security to load a user by email/username. This is the core method Spring Security calls during authentication.
		logger.info("Inside loadUserByUsername {}", username);
		userDetail = userDao.findByEmailId(username); // Fetches the user from DB.
		if (!Objects.isNull(userDetail))
			return new User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
		// Wraps the user into Spring Security's own User object. The third param is a
		// list of roles—empty for now.
		else
			throw new UsernameNotFoundException("User not found.");
	}

	public com.hotel.POJO.User getUserDetail() { // Allows external access to the full user object—not just Spring's subset.
		return userDetail;
	}

}
