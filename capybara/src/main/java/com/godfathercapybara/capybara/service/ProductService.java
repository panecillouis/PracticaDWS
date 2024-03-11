package com.godfathercapybara.capybara.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.godfathercapybara.capybara.model.Comment;
import com.godfathercapybara.capybara.model.Product;

@Service
public class ProductService {

	@Autowired
	private ImageService imageService;

	private AtomicLong nextId = new AtomicLong(1L);
	private ConcurrentHashMap<Long, Product> products = new ConcurrentHashMap<>();

	public Optional<Product> findById(long id) {
		if (this.products.containsKey(id)) {
			return Optional.of(this.products.get(id));
		}
		return Optional.empty();
	}

	public boolean exist(long id) {
		return this.products.containsKey(id);
	}

	public void addComment(long id, Comment comment) {
		if (this.exist(id)) {
			Product product = this.products.get(id);
			List<Comment> comments = product.getComments();
			comments.add(comment);
			product.setComments(comments);
			products.put(id, product);
		}
	}

	public void deleteComment(long id, long idComment) {
		if (this.exist(id)) {
			Product product = this.products.get(id);
			List<Comment> comments = product.getComments();
			comments.removeIf(comment -> comment.getId() == idComment);
			product.setComments(comments);
			products.put(id, product);
		}
	}

	public List<Product> findAll() {
		return this.products.values().stream().toList();
	}

	public List<Product> findByIds(List<Long> ids) {
		List<Product> products = new ArrayList<>();
		for (long id : ids) {
			products.add(this.products.get(id));
		}
		return products;
	}

	public Product save(Product Product, MultipartFile imageField) {

		if (imageField != null && !imageField.isEmpty()) {
			String path = imageService.createImage(imageField);
			Product.setImage(path);
		}

		if (Product.getImage() == null || Product.getImage().isEmpty())
			Product.setImage("no-image.png");

		long id = nextId.getAndIncrement();
		Product.setId(id);
		products.put(id, Product);
		return Product;
	}

	public void delete(long id) {
		products.remove(id);
	}

	public void updateProduct(Product product, long id, MultipartFile imageField) {

		if (imageField != null && !imageField.isEmpty()) {
			String path = imageService.createImage(imageField);
			product.setImage(path);
		}

		if (product.getImage() == null || product.getImage().isEmpty()) {
			Optional<Product> productOptional = findById(id);

			Product existingProduct = productOptional.get();
			if (existingProduct != null) {
				product.setImage(existingProduct.getImage());
			}
		}

		products.put(id, product);

	}

}
