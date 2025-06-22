package com.hotel.rest;

import java.util.List;
///This is a REST controller interface that defines the API contract
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hotel.wrapper.UserWrapper;

@RequestMapping(path = "/user") // meaning all endpoints will be prefixed with /user.
public interface UserRest {

	@PostMapping(path = "/signup")
	public ResponseEntity<String> signUp(@RequestBody(required = true) Map<String, String> requestMap);
	// ResponseEntity<T> is a Spring class used to return HTTP responses with status codes and body content. //signUp(...) → The method name that defines a signup endpoint.
	//@RequestBody is a Spring annotation that tells Spring to map the request’s JSON body into the method argument. 
	//required = true means that the request must contain a body, or an error will be thrown. Map<String, String> requestMap →This parameter stores JSON request data as a key-value pair.
	
	@PostMapping(path = "/login")
	public ResponseEntity<String> login(@RequestBody(required = true) Map<String, String> requestMap);
	
	//Added Manually to list users
	@GetMapping(path = "/get")
	public ResponseEntity<List<UserWrapper>> getAllUsers();
	
	@PostMapping(path = "/update")
	public ResponseEntity<String> update(@RequestBody(required = true) Map<String, String> requestMap);
}
