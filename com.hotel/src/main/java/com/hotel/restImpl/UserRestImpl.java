package com.hotel.restImpl;
//This class implements UserRest and provides the actual logic for handling the signup request
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.constants.HotelConstants;
import com.hotel.rest.UserRest;
import com.hotel.service.UserService;
import com.hotel.utils.HotelUtils;

@RestController //it will handle HTTP requests, making it a Spring MVC controller that handles HTTP requests.
public class UserRestImpl implements UserRest{
	@Autowired //Uses Dependency Injection - It automatically injects UserService, which likely handles business logic related to users.
	UserService userService;
	
	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		try {
			return userService.signUp(requestMap); //Implements signUp(), calling userService.signUp(requestMap), meaning the actual signup logic is in UserService.
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return HotelUtils.getResponseEntity(HotelConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
