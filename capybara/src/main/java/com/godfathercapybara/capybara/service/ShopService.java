package com.godfathercapybara.capybara.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.godfathercapybara.capybara.model.Shop;
import com.godfathercapybara.capybara.model.Product;

@Service
public class ShopService {

	@Autowired
	private ProductService productService;

	private AtomicLong nextId = new AtomicLong(1L);
	private ConcurrentHashMap<Long, Shop> shops = new ConcurrentHashMap<>();

	public Optional<Shop> findById(long id) {
		if (this.shops.containsKey(id)) {
			return Optional.of(this.shops.get(id));
		}
		return Optional.empty();
	}

	public List<Shop> findByIds(List<Long> ids) {
		List<Shop> shops = new ArrayList<>();
		for (long id : ids) {
			shops.add(this.shops.get(id));
		}
		return shops;
	}

	public boolean exist(long id) {
		return this.shops.containsKey(id);
	}

	public List<Shop> findAll() {
		return this.shops.values().stream().toList();
	}

	public Shop save(Shop shop) {
		long id = nextId.getAndIncrement();
		shop.setId(id);
		shops.put(id, shop);
		return shop;
	}

	public void delete(long id) {
		this.shops.remove(id);
	}
	public void addProduct(Product product, long shopId) {
		Shop shop = this.shops.get(shopId);
		List<Product> products = shop.getProducts();
		products.add(product);
		shop.setProducts(products);
		shops.put(shopId, shop);
	}
	public void deleteProduct(long productId, long shopId) {
		
			Shop shop = this.shops.get(shopId);
			List<Product> products = shop.getProducts();
			products.remove(productService.findById(productId).get());
			shop.setProducts(products);
			shops.put(shopId, shop);
	}

}
