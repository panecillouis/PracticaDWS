package com.godfathercapybara.capybara.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import com.godfathercapybara.capybara.model.Comment;
import com.godfathercapybara.capybara.model.Product;
import com.godfathercapybara.capybara.service.ProductService;
import org.springframework.stereotype.Service;
@Service
public class CommentService {
    
	private AtomicLong nextId = new AtomicLong(1L);
	private ConcurrentHashMap<Long, Comment> comments = new ConcurrentHashMap<>();

	public Optional<Comment> findById(long id) {
		if(this.comments.containsKey(id)) {
			return Optional.of(this.comments.get(id));
		}
		return Optional.empty();
	}

	public List<Comment> findByIds(List<Long> ids){
		List<Comment> comments = new ArrayList<>();
		for(long id : ids){
			comments.add(this.comments.get(id));
		}
		return comments;
	}
	
	public boolean exist(long id) {
		return this.comments.containsKey(id);
	}

	public List<Comment> findAll() {
		return this.comments.values().stream().toList();
	}

	public Comment save(Comment comment) {
		long id = nextId.getAndIncrement();
		comment.setId(id);
		comments.put(id, comment);
		return comment;
	}

	public void delete(long id) {
		this.comments.remove(id);
	}
	
}










