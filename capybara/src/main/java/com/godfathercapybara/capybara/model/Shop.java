package com.godfathercapybara.capybara.model;

import java.util.ArrayList;
import java.util.List;

public class Shop {

	private Long id = null;
	
	private String name;
	
	private String address;

 	private List<Product> products;

	public Shop() {}

	public Shop(String name, String address) {
		this.name = name;
		this.address = address;
		this.products = new ArrayList<>();
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
		return products;
	}
	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
