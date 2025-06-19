package com.hotel.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.hotel.POJO.User;

public interface UserService {

	ResponseEntity<String> signUp(Map<String, String> requestMap);
	
	//Added Manually
	ResponseEntity<List<User>> getAllUsers();

}
	