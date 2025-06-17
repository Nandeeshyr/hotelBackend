package com.hotel.serviceImpl;

import java.util.Map;
import java.util.Objects;

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
@Service // The @Service annotation in Spring is used to mark a class as a service
			// provider, indicating that it contains business logic.
public class UserServiceImpl implements UserService {
	

	@Autowired
	UserDao userDao;

	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		
		log.info("Inside signUp {}", requestMap);
		try {
		if (validateSignUpMap(requestMap)) {
			User user = userDao.findByEmailId(requestMap.get("email"));
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
}
