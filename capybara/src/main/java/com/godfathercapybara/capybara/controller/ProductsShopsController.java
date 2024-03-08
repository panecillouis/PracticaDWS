package com.godfathercapybara.capybara.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.godfathercapybara.capybara.model.Product;
import com.godfathercapybara.capybara.model.Shop;
import com.godfathercapybara.capybara.service.ProductService;
import com.godfathercapybara.capybara.service.ShopService;


@RequestMapping("/api")
@RestController
public class ProductsShopsController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ProductService productService;
    
    @JsonView(Product.Basico.class)
	@GetMapping("/products/")
	public List<Product> getProducts() {
       List<Product> products = productService.findAll();
		return products;
	}

	@JsonView(Shop.Basico.class)
	@GetMapping("/shops/")
	public List<Shop> getShops() {
		List<Shop> shops = shopService.findAll();
		return shops;
	}

	interface ProductDetail extends Product.Basico, Product.Shops, Shop.Basico {
	}

	@JsonView(ProductDetail.class)
	@GetMapping("/products/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable long id) {
		List<Product> products = productService.findAll();
		int productId = Integer.parseInt(String.valueOf(id));
		Product product = products.get(productId);
		return ResponseEntity.ok(product);
	}

	interface ShopDetail extends Shop.Basico, Shop.Products, Product.Basico {
	}

	@JsonView(ShopDetail.class)
	@GetMapping("/shops/{id}")
	public ResponseEntity<Shop> getShopById(@PathVariable long id) {
		List<Shop> shops = shopService.findAll();
		int shopId = Integer.parseInt(String.valueOf(id));
		Shop shop = shops.get(shopId);
		return ResponseEntity.ok(shop);
	}

}
