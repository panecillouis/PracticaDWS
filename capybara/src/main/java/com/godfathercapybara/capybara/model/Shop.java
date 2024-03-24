package com.godfathercapybara.capybara.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
@Entity
public class Shop {

	public interface Basic {
	}

	public interface Products {
	}
	@Id 
	@JsonView(Basic.class)
	private Long id;

	@JsonView(Basic.class)
	private String name;

	@JsonView(Basic.class)
	private String address;

	@ManyToMany(cascade=CascadeType.MERGE)
	@JsonView(Products.class)
	private List<Product> products = new ArrayList<>();

	public Shop() {
	}

	public Shop(String name, String address) {
		super();
		this.name = name;
		this.address = address;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public List<Product> getProducts() {
		return this.products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public String toString() {
		return "Tienda {id=" + id + ", nombre=" + name + ", direccion=" + address + "}";
	}
}
