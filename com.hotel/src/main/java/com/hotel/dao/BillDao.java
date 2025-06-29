package com.hotel.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.POJO.Bill;

public interface BillDao extends JpaRepository<Bill, Integer>{

}
