package com.godfathercapybara.capybara.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.godfathercapybara.capybara.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
