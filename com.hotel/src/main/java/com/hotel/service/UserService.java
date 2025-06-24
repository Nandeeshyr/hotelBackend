package com.hotel.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.hotel.wrapper.UserWrapper;

public interface UserService {

	ResponseEntity<String> signUp(Map<String, String> requestMap);
	
	//Added Manually to list users
	ResponseEntity<List<UserWrapper>> getAllUsers();

	ResponseEntity<String> login(Map<String, String> requestMap);

	ResponseEntity<String> update(Map<String, String> requestMap);

	ResponseEntity<String> checkToken();

	ResponseEntity<String> changePassword(Map<String, String> requestMap);

	ResponseEntity<String> forgotPassword(Map<String, String> requestMap);

}
	