package com.godfathercapybara.capybara.model;

import com.fasterxml.jackson.annotation.JsonView;

public class Comment {
	public interface Basic {
	}

	@JsonView(Basic.class)
	private Long id = null;
	@JsonView(Basic.class)
	private String comment;
	@JsonView(Basic.class)
	private String author;

	public Comment() {
	}

	public Comment(String comment, String author) {
		super();
		this.comment = comment;
		this.author = author;

	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "Comment [id=" + id + ", comment=" + comment + ", author=" + author + "]";
	}

}
