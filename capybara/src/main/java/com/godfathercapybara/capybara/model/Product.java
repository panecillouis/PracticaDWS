package com.godfathercapybara.capybara.model;

import java.util.List;

public class Product {
    private Long id;
    private String description;
    private String image;
    private String type;
    private String name;
    private double price;
    private List <Shop> shops;

    public Product( String name, String description, String type, double price) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.price = price;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getName() {
    return name;
    }

    public void setName(String name) {
    this.name = name;
    }
    
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public List<Shop> getShops() {
		return shops;
	}

	public void setShops(List<Shop> shops) {
		this.shops = shops;
	}
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price +
                '}';
    }
    
}
