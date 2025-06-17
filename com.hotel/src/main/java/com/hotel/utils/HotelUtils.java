package com.hotel.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class HotelUtils {

	private HotelUtils() {
		
	}
	public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus){
		return new ResponseEntity<String>("{\"message\":\""+responseMessage+"\"}",httpStatus);
	}
}
