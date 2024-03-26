package com.godfathercapybara.capybara.model;

import java.sql.Blob;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;


@Entity
public class Capybara {

	@Id  
	private Long id;
	private String sex;
	private String color;
	private double price;
	private String description;
	private String name;
	private String analytics;
	private String image;
	@Lob @JsonIgnore
	private Blob imageFile;
	private boolean isSponsored = false;

	public Capybara() {
	}

	public Capybara(String sex, String color, double price, String description, String name) {
		super();
		this.sex = sex;
		this.color = color;
		this.price = price;
		this.description = description;
		this.name = name;

	}

	public Capybara(String sex, String color, double price, String description, String name, boolean isSponsored) {
		super();
		this.sex = sex;
		this.color = color;
		this.price = price;
		this.description = description;
		this.name = name;
		this.isSponsored = isSponsored;

	}

	public boolean getIsSponsored() {
		return this.isSponsored;
	}

	public void setIsSponsored(boolean isSponsored) {
		this.isSponsored = isSponsored;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSex() {
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getColor() {
		return this.color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Blob getImageFile() {
		return this.imageFile;
	}
	
	public void setImageFile(Blob imageFile) {
		this.imageFile = imageFile;
	}
	public String getImage() {
		return this.image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getAnalytics() {
		return this.analytics;
	}
	public void setAnalytics(String analytics) {
		this.analytics = analytics;
	}

	@Override
	public String toString() {
		return "Capybara [id=" + id + ", sex=" + sex + ", color=" + color + ", price=" + price + ", description="
				+ description + ", name=" + name + "]";
	}
}
