package com.godfathercapybara.capybara.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.godfathercapybara.capybara.model.Comment;
import com.godfathercapybara.capybara.repository.CommentRepository;

@Service
public class CommentService {
	@Autowired
	private CommentRepository commentRepository;
	private AtomicLong nextId = new AtomicLong(1L);

	public Optional<Comment> findById(long id) {
		if (this.exist(id)) {
			return Optional.of(commentRepository.findById(id).orElseThrow());
		}
		return Optional.empty();
	}

	public List<Comment> findByIds(List<Long> ids) {
		List<Comment> comments = new ArrayList<>();
		for (long id : ids) {
			comments.add(commentRepository.findById(id).orElseThrow());
		}
		return comments;
	}

	public boolean exist(long id) {
		return commentRepository.existsById(id);
	}

	public List<Comment> findAll() {
		return this.commentRepository.findAll();
	}

	public Comment save(Comment comment) {
		long id = nextId.getAndIncrement();
		comment.setId(id);
		commentRepository.save(comment);
		return comment;
	}

	public void delete(long id) {
		commentRepository.deleteById(id);
	}

}
