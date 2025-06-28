package com.hotel.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.hotel.JWT.JwtFilter;
import com.hotel.POJO.Category;
import com.hotel.constants.HotelConstants;
import com.hotel.dao.CategoryDao;
import com.hotel.service.CategoryService;
import com.hotel.utils.HotelUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
	private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

	@Autowired
	CategoryDao categoryDao;

	@Autowired
	JwtFilter jwtFilter;

	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
		try {
			if (jwtFilter.isAdmin()) {
				if (isValidForAdd(requestMap)) { //Calls a helper method to validate that the request contains the required "name" field.
					categoryDao.save(createCategoryForAdd(requestMap)); //Converts the request map into a Category object and saves it to the database.
					return HotelUtils.getResponseEntity("Category Added Successfully", HttpStatus.OK);
				}
			} else {
				return HotelUtils.getResponseEntity(HotelConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return HotelUtils.getResponseEntity(HotelConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filteredValue) {
		logger.info("Categories");
		try {
			if (!Strings.isNullOrEmpty(filteredValue) && filteredValue.equalsIgnoreCase("true")) { //If filteredValue is "true", it fetches a filtered list; otherwise, it fetches all categories.
				return new ResponseEntity<>(categoryDao.getAllCategory(), HttpStatus.OK);
			}
			return new ResponseEntity<>(categoryDao.findAll(), HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
		try {
			if (jwtFilter.isAdmin()) {
				if (isValidForUpdate(requestMap)) { //Validates that both "name" and "id" are present in the request.
					Optional<Category> optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
					if (optional.isPresent()) {
						categoryDao.save(createCategoryForUpdate(requestMap));
						return HotelUtils.getResponseEntity("Category updated Successfully", HttpStatus.OK);
					} else {
						return HotelUtils.getResponseEntity("Category ID does not exist", HttpStatus.OK);
					}
				}
				return HotelUtils.getResponseEntity(HotelConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			} else {
				return HotelUtils.getResponseEntity(HotelConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return HotelUtils.getResponseEntity(HotelConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Refactored validation methods
	private boolean isValidForAdd(Map<String, String> requestMap) {
		return requestMap.containsKey("name");
	}

	private boolean isValidForUpdate(Map<String, String> requestMap) {
		return requestMap.containsKey("name") && requestMap.containsKey("id");
	}

	// Refactored category creation methods
	private Category createCategoryForAdd(Map<String, String> requestMap) {
		Category category = new Category();
		category.setName(requestMap.get("name"));
		return category;
	}

	private Category createCategoryForUpdate(Map<String, String> requestMap) {
		Category category = new Category();
		category.setId(Integer.parseInt(requestMap.get("id")));
		category.setName(requestMap.get("name"));
		return category;
	}
}
