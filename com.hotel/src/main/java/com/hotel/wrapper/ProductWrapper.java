package com.hotel.wrapper;

import lombok.Data;

@Data
public class ProductWrapper {

	private Integer id;

	private String name;

	private String description;

	private Integer price;

	private String status;

	private Integer categoryId;

	private String categoryName;

	public ProductWrapper() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProductWrapper(Integer id, String name, String description, Integer price, String status, Integer categoryId,
			String categoryName) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.status = status;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public ProductWrapper(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public ProductWrapper(Integer id, String name, String description, Integer price) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
	}

}
