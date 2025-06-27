package com.hotel.service;

import java.util.List;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.hotel.POJO.Category;


public interface CategoryService {

	ResponseEntity<String> addNewCategory(Map<String, String> requsetMap);

	ResponseEntity<List<Category>> getAllCategory(String filteredValue);

	ResponseEntity<String> updateCategory(Map<String, String> requsetMap);

}
