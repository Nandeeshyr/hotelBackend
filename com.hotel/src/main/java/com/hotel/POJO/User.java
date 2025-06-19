package com.hotel.POJO; //Defines the package structure for better organization. POJO stands for "Plain Old Java Object" — a simple Java class without any heavy dependencies.

import java.io.Serializable; //Implements serialization so the object can be converted to bytes — useful for caching, network transfers, or saving state.

import org.hibernate.annotations.DynamicInsert; //Hibernate-specific annotations to optimize SQL operations: only non-null fields are inserted or updated.
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*; //These are JPA annotations for ORM (Object-Relational Mapping) — used to map the class and its fields to a database table.

import lombok.Data; //auto-generates boilerplate code — like getters, setters, toString(), etc., via the @Data annotation.

@NamedQuery(name="User.findByEmailId", query="select u from User u where u.email=:email")

@Data /*Generates constructors, getters, setters, toString(), equals(), and hashCode() methods automatically. No need for manually defining these methods(removes below commented part).*/
@Entity // Marks this class as a JPA entity, meaning it will be mapped to a database table.
@DynamicInsert /*Ensures only non-null values are included in INSERT queries. Prevents default values in the database from being overridden.*/
@DynamicUpdate /*Ensures only changed values are included in UPDATE queries. Optimizes database operations by preventing unnecessary updates.*/
@Table(name = "user") // Creates DB table with this
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


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
