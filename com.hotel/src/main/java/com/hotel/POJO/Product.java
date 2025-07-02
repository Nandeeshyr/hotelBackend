package com.hotel.POJO;

import java.io.Serializable;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;

@NamedQuery(name = "Product.getAllProduct", query = "select new com.hotel.wrapper.ProductWrapper(p.id,p.name,p.description,p.price,p.status,p.category.id,p.category.name) from Product p")

@NamedQuery(name = "Product.updateProductStatus", query = "update Product p set p.status=:status where p.id=:id")

//Fetches all active products (i.e., status = 'true') that belong to a specific category. Returns id, name
@NamedQuery(name = "Product.getProductByCategory", query = "select new com.hotel.wrapper.ProductWrapper(p.id,p.name) from Product p where p.category.id=:id and p.status='true'")

//Fetches a specific product by its ID, regardless of its status. Returns id, name, description, price
@NamedQuery(name = "Product.getProductById", query = "select new com.hotel.wrapper.ProductWrapper(p.id,p.name,p.description,p.price) from Product p where p.id=:id")

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "product")
public class Product implements Serializable {

	public static final Long serialVersionUid = 123456L;
	// Serializable: Allows the object to be converted into a byte stream, which is
	// useful for caching, session storage, or network transmission.

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "name")
	private String name;

	@ManyToOne(fetch = FetchType.LAZY) // A Product like “Coffee” belongs to a Category like “Beverages”. Category data
										// is loaded only when the category field is accessed (e.g., product.getCategory()).
	@JoinColumn(name = "category_fk", nullable = false) // Specifies the foreign key column name in the database table
	private Category category;

	@Column(name = "description")
	private String description;

	@Column(name = "price")
	private Integer price;

	@Column(name = "status")
	private String status;

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

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
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

	public static Long getSerialversionuid() {
		return serialVersionUid;
	}

}
