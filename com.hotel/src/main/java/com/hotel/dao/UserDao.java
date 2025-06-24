package com.hotel.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import com.hotel.POJO.User;
import com.hotel.wrapper.UserWrapper;

import jakarta.transaction.Transactional;

//JpaRepository is a Spring Data interface that provides basic CRUD operations (like save(), findById(), delete(), etc.) so you don't have to write them yourself.
public interface UserDao extends JpaRepository<User,Integer>{
	//By extending JpaRepository<User, Integer>, it automatically inherits all CRUD methods for the User entity whose primary key (id) is of type Integer.
	User findByEmailId(@Param("email") String email);
	
	List<UserWrapper> getAllUsers();
	
	List<String> getAllAdmin();

	@Transactional //JPA needs to manage changes in a transaction. If anything goes wrong, it can roll back.
	@Modifying //Spring would expect a return result (like a List<T>), but here we’re executing a void-type query that returns how many rows were affected.
	Integer updateStatus(@Param("status") String status, @Param("id") Integer id);
	
	User findByEmail(String email);
}
