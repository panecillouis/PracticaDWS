package com.godfathercapybara.capybara.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.godfathercapybara.capybara.model.Comment;
import com.godfathercapybara.capybara.model.Product;
import com.godfathercapybara.capybara.repository.ProductRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@Service
public class ProductService {

	
	@Autowired
	private EntityManager entityManager;

	@Autowired
	private ProductRepository productRepository;

	private AtomicLong nextId = new AtomicLong(1L);

	public Optional<Product> findById(long id) {
		if (this.exist(id)) {
			return Optional.of(productRepository.findById(id).orElseThrow());
		}
		return Optional.empty();
	}

	public boolean exist(long id) {
		return productRepository.existsById(id);
	}

	public void addComment(long id, Comment comment) {
		if (this.exist(id)) {
			Product product = productRepository.findById(id).orElseThrow();
			List<Comment> comments = product.getComments();
			comments.add(comment);
			product.setComments(comments);
			productRepository.save(product);
			
		}
	}

	public void deleteComment(long id, long idComment) {
		if (this.exist(id)) {
			Product product = productRepository.findById(id).orElseThrow();
			List<Comment> comments = product.getComments();
			comments.removeIf(comment -> comment.getId() == idComment);
			product.setComments(comments);
			productRepository.save(product);
		}
	}

	public List<Product> findAll() {
		return productRepository.findAll();
	}

	@SuppressWarnings("unchecked")
	public List<Product> findAll(Boolean comment, Double price, String type) {
		StringBuilder query = new StringBuilder("SELECT DISTINCT p.* FROM product p");
		if(comment!=null) {
			query.append(" INNER JOIN product_comments pc ON p.id = pc.product_id");
		}
		if(price!=null || isNotEmptyField(type)) {
			query.append(" WHERE");
		}
		if(price!=null) {
			query.append(" price <= :price AND");
		}
		
		if(isNotEmptyField(type)) {
			query.append(" type= :type AND");
		}
		if (query.toString().endsWith("AND")) {
			query.setLength(query.length() - 4);
		}
		Query jpaQuery = entityManager.createNativeQuery(query.toString(), Product.class);
		if(price!=null) {
            jpaQuery.setParameter("price", price);
        }
		if(isNotEmptyField(type)) {
            jpaQuery.setParameter("type", type);
        }
		return jpaQuery.getResultList();
	}


	private boolean isNotEmptyField(String field) {
		return field != null && !field.isEmpty();
	}
	

	public List<Product> findByIds(List<Long> ids) {
		List<Product> products = new ArrayList<>();
		for (long id : ids) {
			products.add(productRepository.findById(id).orElseThrow());
		}
		return products;
	}

	public Product save(Product product, MultipartFile imageField) throws IOException {
		
		if (imageField != null && !imageField.isEmpty()) {
			product.setImage(imageField.getOriginalFilename());
			product.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));;
		}

		else if (product.getImage() == null || product.getImage().isEmpty())
		{	
			product.setImage("no-image.png");
			product.setImageFile(null);
		}
		long id = nextId.getAndIncrement();
		product.setId(id);
		productRepository.save(product);
		return product;
		
	}

	public void delete(long id) {
		productRepository.deleteById(id);
	}

	public void updateProduct(Product product, long id, MultipartFile imageField) throws IOException {

		if (imageField != null && !imageField.isEmpty()) {
			product.setImage(imageField.getOriginalFilename());
        	product.setImageFile(BlobProxy.generateProxy(imageField.getInputStream(), imageField.getSize()));
		}
		
		else if (product.getImage() == null || product.getImage().isEmpty()) {
			Product existingProduct = productRepository.findById(id).orElseThrow();
			product.setImageFile(existingProduct.getImageFile());
			product.setImage(existingProduct.getImage());
		}
		
		productRepository.save(product);

	}

	

}
