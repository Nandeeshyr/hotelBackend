package com.hotel.serviceImpl;

import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.hotel.JWT.CustomerUserDetailsService;
import com.hotel.JWT.JwtFilter;
import com.hotel.JWT.JwtUtil;
import com.hotel.POJO.User;
import com.hotel.constants.HotelConstants;
import com.hotel.dao.UserDao;
import com.hotel.service.UserService;
import com.hotel.utils.EmailUtils;
import com.hotel.utils.HotelUtils;
import com.hotel.wrapper.UserWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service // Marks the class as a service component in Spring — it houses the business
			// logic.
public class UserServiceImpl implements UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired // UserDao is injected here to perform database operations using JPA (like
				// checking and saving users).
	UserDao userDao; // userDao is a gateway or bridge to the database.

	@Autowired
	CustomerUserDetailsService customerUserDetailsService;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	JwtFilter jwtFilter;
	
	@Autowired
	EmailUtils emailUtils;

	@Override // Accepts user data from the Controller as a Map<String, String>, where keys
				// are like "email", "password" etc. Returns a ResponseEntity<String> — Spring’s
				// HTTP response wrapper.
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		// requestMap is a Java Map<String, String> object that acts as a container for
		// data sent from the client during a signup request.
		logger.info("Inside signUp {}", requestMap);
		try {
			if (validateSignUpMap(requestMap)) {
				User user = userDao.findByEmailId(requestMap.get("email")); // Searches DB for an existing user by
																			// email.
				if (Objects.isNull(user)) {
					userDao.save(getUserFromMap(requestMap));
					return HotelUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
				} else {
					return HotelUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
				}
			} else {
				return HotelUtils.getResponseEntity(HotelConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return HotelUtils.getResponseEntity(HotelConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	private boolean validateSignUpMap(Map<String, String> requestMap) {
		if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber") && requestMap.containsKey("email")
				&& requestMap.containsKey("password")) {
			return true;
		}
		return false;
	}

	private User getUserFromMap(Map<String, String> requestMap) {
		User user = new User();
		user.setName(requestMap.get("name"));
		user.setContactNumber(requestMap.get("contactNumber"));
		user.setEmail(requestMap.get("email"));
		user.setPassword(passwordEncoder.encode(requestMap.get("password"))); //user.setPassword(requestMap.get("password")); - Replace this to store raw password in DB
		user.setStatus("false");
		user.setRole("user");

		return user;
	}

	// Added Manually to list users
	@Override
	public ResponseEntity<List<UserWrapper>> getAllUsers() {
		try {
			if (jwtFilter.isAdmin()) {
				return new ResponseEntity<>(userDao.getAllUsers(), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}// Added Manually to list users

	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {
		logger.info("Inside Login {}", requestMap);
		try { // like Authentication auth = authenticationManager.authenticate(token)
			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
			if (auth.isAuthenticated()) {
				if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
					return new ResponseEntity<String>(
							"{\"token\":\""
									+ jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
											customerUserDetailsService.getUserDetail().getRole())
									+ "\"}",
							HttpStatus.OK);
				} else {
					return new ResponseEntity<String>("{\"message\":\"" + "Wait for admin approval." + "\"}",
							HttpStatus.BAD_REQUEST);
				}
			}

		} catch (Exception ex) {
			logger.error("{}", ex);
		}
		return new ResponseEntity<String>("{\"message\":\"" + "Bad Credentials." + "\"}", HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			if (jwtFilter.isAdmin()) {
				Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id"))); //It verifies the user exists before trying to update.
				if (!optional.isEmpty()) {
					userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id"))); //Uses the @NamedQuery to directly update the status field in the DB.
					sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());
					return HotelUtils.getResponseEntity("User status updated successfully.", HttpStatus.OK);
				} else {
					HotelUtils.getResponseEntity("User Id does not exist.", HttpStatus.OK);
				}
			} else {
				return HotelUtils.getResponseEntity(HotelConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return HotelUtils.getResponseEntity(HotelConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
		allAdmin.remove(jwtFilter.getCurrentUser());
		if(status!=null && status.equalsIgnoreCase("true")) {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved.","USER:- "+user+" \n is approved by \n ADMIN:- "+jwtFilter.getCurrentUser(), allAdmin);
		}else {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled.","USER:- "+user+" \n is disabled by \n ADMIN:- "+jwtFilter.getCurrentUser(), allAdmin);
		}
		
	}

	@Override
	public ResponseEntity<String> checkToken() {
			return HotelUtils.getResponseEntity("true", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
		try {
			User userObj = userDao.findByEmail(jwtFilter.getCurrentUser());
			if(!userObj.equals(null)) {
				if(passwordEncoder.matches(requestMap.get("oldPassword"), userObj.getPassword())) {
				    userObj.setPassword(passwordEncoder.encode(requestMap.get("newPassword")));
				    userDao.save(userObj);
				    return HotelUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);
				}
				return HotelUtils.getResponseEntity("Incorrect Old Password", HttpStatus.BAD_REQUEST);
			}
			return HotelUtils.getResponseEntity(HotelConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return HotelUtils.getResponseEntity(HotelConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private String generateTemporaryPassword() {
	    // Simple example — you can customize this for complexity
	    return UUID.randomUUID().toString().substring(0, 8); // e.g., "a9b1c3d4"
	}


	@Override
	public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
	    try {
	        User user = userDao.findByEmail(requestMap.get("email"));
	        if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
	            String tempPassword = generateTemporaryPassword(); // Step 1: generate a new password
	            user.setPassword(passwordEncoder.encode(tempPassword)); // Step 2: encode and save to DB
	            userDao.save(user);

	            emailUtils.forgotMail(
	                user.getEmail(),
	                "Temporary Password - Hotel Management System",
	                tempPassword // Step 3: send raw password to user
	            );

	            return HotelUtils.getResponseEntity("Check your mail for temporary credentials.", HttpStatus.OK);
	        } else {
	            return HotelUtils.getResponseEntity("Email not found", HttpStatus.BAD_REQUEST);
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    return HotelUtils.getResponseEntity(HotelConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	

}
