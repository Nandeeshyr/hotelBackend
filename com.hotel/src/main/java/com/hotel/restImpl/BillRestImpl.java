package com.hotel.restImpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.constants.HotelConstants;
import com.hotel.rest.BillRest;
import com.hotel.service.BillService;
import com.hotel.utils.HotelUtils;

@RestController
public class BillRestImpl implements BillRest {

	@Autowired
	BillService billService;

	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		try {
			return billService.generateReport(requestMap);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return HotelUtils.getResponseEntity(HotelConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
