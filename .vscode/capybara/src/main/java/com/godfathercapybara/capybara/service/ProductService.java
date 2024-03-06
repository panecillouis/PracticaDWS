package com.godfathercapybara.capybara.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.godfathercapybara.capybara.model.Capybara;
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

	public List<Product> findAll() {
		return new ArrayList<>(this.products.values());
	}

	public List<Product> findByIds(List<Long> ids) {
		List<Product> products = new ArrayList<>();
		for (long id : ids) {
			products.add(this.products.get(id));
		}
		return products;
	}

	public Product save(Product product, MultipartFile imageField) {

		if (imageField != null && !imageField.isEmpty()) {
			String path = imageService.createImage(imageField);
			product.setImage(path);
		}

		if (product.getImage() == null || product.getImage().isEmpty()) {
			product.setImage("no-image.png");
		}

		long id = nextId.getAndIncrement();
		product.setId(id);
		products.put(id, product);
		return product;
	}

	public void delete(long id) {
		products.remove(id);
	}
		
    
}
