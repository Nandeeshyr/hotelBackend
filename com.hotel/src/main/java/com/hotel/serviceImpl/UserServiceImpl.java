package com.hotel.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hotel.POJO.User;
import com.hotel.constants.HotelConstants;
import com.hotel.dao.UserDao;
import com.hotel.service.UserService;
import com.hotel.utils.HotelUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service // Marks the class as a service component in Spring — it houses the business logic.
public class UserServiceImpl implements UserService {
	 private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired //UserDao is injected here to perform database operations using JPA (like checking and saving users).
	UserDao userDao; //userDao is a gateway or bridge to the database.

	@Override //Accepts user data from the Controller as a Map<String, String>, where keys are like "email", "password" etc. Returns a ResponseEntity<String> — Spring’s HTTP response wrapper.
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		//requestMap is a Java Map<String, String> object that acts as a container for data sent from the client during a signup request.
		logger.info("Inside signUp {}", requestMap);
		try {
		if (validateSignUpMap(requestMap)) {
			User user = userDao.findByEmailId(requestMap.get("email")); //Searches DB for an existing user by email.
			if(Objects.isNull(user)) {
				userDao.save(getUserFromMap(requestMap));
				return HotelUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);
			}else {
				return HotelUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
			}
		} else {
			return HotelUtils.getResponseEntity(HotelConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
		}
	}catch(Exception ex){
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
		user.setPassword(requestMap.get("password"));
		user.setStatus("false");
		user.setRole("user");

		return user;
	}
	//Added Manually
	public ResponseEntity<List<User>> getAllUsers() {
	    try {
	        return new ResponseEntity<>(userDao.findAll(), HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}//Added Manually
}
