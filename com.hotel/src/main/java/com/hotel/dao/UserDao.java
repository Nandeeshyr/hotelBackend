package com.hotel.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.hotel.POJO.User;

//JpaRepository is a Spring Data interface that provides basic CRUD operations (like save(), findById(), delete(), etc.) so you don't have to write them yourself.
public interface UserDao extends JpaRepository<User,Integer>{
	//By extending JpaRepository<User, Integer>, it automatically inherits all CRUD methods for the User entity whose primary key (id) is of type Integer.
	User findByEmailId(@Param("email") String email);
}
