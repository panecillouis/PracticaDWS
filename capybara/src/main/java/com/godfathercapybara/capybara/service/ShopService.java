package com.godfathercapybara.capybara.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.godfathercapybara.capybara.model.Product;
import com.godfathercapybara.capybara.model.Shop;
import com.godfathercapybara.capybara.model.User;
import com.godfathercapybara.capybara.repository.ShopRepository;
import com.godfathercapybara.capybara.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class ShopService {

	@Autowired
	private ProductService productService;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private ShopRepository shopRepository;

	@Autowired
	private UserService userService;
	
	private AtomicLong nextId = new AtomicLong(1L);

	public Optional<Shop> findById(long id) {
		if (this.exist(id)) {
			return Optional.of(shopRepository.findById(id).orElseThrow());
		}
		return Optional.empty();
	}

	public List<Shop> findByIds(List<Long> ids) {
		List<Shop> shops = new ArrayList<>();
		for (long id : ids) {
			shops.add(shopRepository.findById(id).orElseThrow());
		}
		return shops;
	}

	public boolean exist(long id) {
		return shopRepository.existsById(id);
	}

	public List<Shop> findAll() {
		return shopRepository.findAll();
	}
	@SuppressWarnings("unchecked")
	public List<Shop> findAll(String address) {
		StringBuilder query = new StringBuilder("SELECT * FROM shop");
		if(isNotEmptyField(address)) {
			query.append(" WHERE");
		}
		if(isNotEmptyField(address)) {
			query.append(" address LIKE :address");
		}
		Query jpaQuery = entityManager.createNativeQuery(query.toString(), Shop.class);
		if (address != null) {
			jpaQuery.setParameter("address", "%" + address + "%");
		}
		return jpaQuery.getResultList();
	}

	private boolean isNotEmptyField(String field) {
		return field != null && !field.isEmpty();
	}


	public Shop save(Shop shop) {
		long id = nextId.getAndIncrement();
		shop.setId(id);
		shopRepository.save(shop);
		return shop;
	}

	public void delete(long id) {
		shopRepository.deleteById(id);
	}
	public void addProduct(Product product, long shopId) {
		Shop shop = shopRepository.findById(shopId).orElseThrow();
		List<Product> products = shop.getProducts();
		products.add(product);
		shop.setProducts(products);
		shopRepository.save(shop);
	}
	public void deleteProduct(long productId, long shopId) {
		
			Shop shop = shopRepository.findById(shopId).orElseThrow();
			List<Product> products = shop.getProducts();
			products.remove(productService.findById(productId).get());
			shop.setProducts(products);
			shopRepository.save(shop);
	}
	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if(principal != null) {
		
			model.addAttribute("logged", true);	
			String name = principal.getName();
			Optional<User> userOptional = userService.findByUsername(name);
			User user= userOptional.get();
			model.addAttribute("user", user);		
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
			
		} else {
			model.addAttribute("logged", false);
		}
	}

}
