package com.godfathercapybara.capybara.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.godfathercapybara.capybara.model.Shop;

@Service
public class ShopService {

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

}
