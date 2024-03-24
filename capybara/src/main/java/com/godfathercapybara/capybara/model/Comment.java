package com.godfathercapybara.capybara.model;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity 
public class Comment {
	public interface Basic {
	}

	@Id @JsonView(Basic.class) 
	private Long id;
	@JsonView(Basic.class)
	private String text;
	@JsonView(Basic.class)
	private String author;

	public Comment() {
	}

	public Comment(String text, String author) {
		super();
		this.text = text;
		this.author = author;

	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", comment=" + text + ", author=" + author + "]";
	}

}
