package com.godfathercapybara.capybara.model;


public class Capybara {

		private Long id = null;
		private String sex;
		private String color;
		private String photo;
		private double price;
		private String description;
		private String name;
		private String image;
		private String godfather = null;

		public Capybara() {}

		public Capybara(String sex, String color, double price, String description, String name) {
			super();
			this.sex = sex;
			this.color = color;
			this.price = price;
			this.description = description;
			this.name = name;
	
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

		public String getPhoto() {
			return this.photo;
		}

		public void setPhoto(String photo) {
			this.photo = photo;
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
		public String getImage() {
			return this.image;
		}

		public void setImage(String image) {
			this.image = image;
		}
		public String getGodfahter()
		{
			return this.godfather;
		}
		public void setGodfather(String godfather)
		{
			this.godfather = godfather;
		}
		

		@Override
		public String toString() {
			return "Capybara [id=" + id + ", sex=" + sex + ", color=" + color + ", godfather=" + godfather + ", price=" + price + ", description=" + description + ", name=" + name +  "]";
		}
	}
