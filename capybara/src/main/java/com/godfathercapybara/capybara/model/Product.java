package com.godfathercapybara.capybara.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

@Entity
public class Product {

    public interface Basic {
    }

    public interface Shops {
    }

    public interface Comments {
    }

    @Id  @JsonView(Basic.class)
    private Long id;

    @JsonView(Basic.class)
    private String description;

    @JsonView(Basic.class)
    private String image;

    @Lob @JsonIgnore
	private Blob imageFile;

    @JsonView(Basic.class)
    private String type;

    @JsonView(Basic.class)
    private String name;

    @JsonView(Basic.class)
    private double price;

    @JsonView(Shops.class)
    @ManyToMany(cascade=CascadeType.MERGE)
    private List<Shop> shops = new ArrayList<>();

    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
    @JsonView(Comments.class)
    public List<Comment> comments = new ArrayList<>();

    public Product() {
    }

    public Product(String name, String description, String type, double price) {
        super();
        this.name = name;
        this.description = description;
        this.type = type;
        this.price = price;
    }
    public Product (Long id)
    {
        this.id = id;
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
    public Blob getImageFile() {
		return this.imageFile;
	}
	
	public void setImageFile(Blob imageFile) {
		this.imageFile = imageFile;
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
        return this.shops;
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }

    public List<Comment> getComments() {
        return new ArrayList<>(comments);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", type='" + type + '\'' +
                ", price=" + price + ",comentarios" + this.comments + ",shops" + this.shops +
                '}';
    }

}
