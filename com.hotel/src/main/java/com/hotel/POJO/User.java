package com.hotel.POJO;

import java.io.Serializable;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data /*Generates getters, setters, toString(), equals(), and hashCode() methods automatically. Removes the need for manually defining these methods(removes below commented part).*/
@Entity // Marks this class as a JPA entity, meaning it will be mapped to a database table.
@DynamicInsert /*Ensures only non-null values are included in INSERT queries. Prevents default values in the database from being overridden.*/
@DynamicUpdate /*Ensures only changed values are included in UPDATE queries. Optimizes database operations by preventing unnecessary updates.*/
@Table(name = "user")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	/*Defines how the primary key is generated. IDENTITY means the database will automatically generate the ID (e.g., auto-increment in MySQL).*/
	@Column(name = "id")
	private Integer id;

	@Column(name = "name")
	private String name;

	@Column(name = "contactNumber")
	private String contactNumber;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	private String password;

	@Column(name = "status")
	private String status;

	@Column(name = "role")
	private String role;
	
//	public User(Integer id, String name, String contactNumber, String email, String password, String status,
//			String role) {
//		super();
//		this.id = id;
//		this.name = name;
//		this.contactNumber = contactNumber;
//		this.email = email;
//		this.password = password;
//		this.status = status;
//		this.role = role;
//	}
//
//	public Integer getId() {
//		return id;
//	}
//
//	public void setId(Integer id) {
//		this.id = id;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public String getContactNumber() {
//		return contactNumber;
//	}
//
//	public void setContactNumber(String contactNumber) {
//		this.contactNumber = contactNumber;
//	}
//
//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//
//	public String getStatus() {
//		return status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}
//
//	public String getRole() {
//		return role;
//	}
//
//	public void setRole(String role) {
//		this.role = role;
//	}
//
//	public static long getSerialversionuid() {
//		return serialVersionUID;
//	}
	
}
