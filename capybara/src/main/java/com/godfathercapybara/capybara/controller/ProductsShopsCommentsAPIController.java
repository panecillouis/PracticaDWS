package com.godfathercapybara.capybara.controller;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonView;
import com.godfathercapybara.capybara.model.Comment;
import com.godfathercapybara.capybara.model.Product;
import com.godfathercapybara.capybara.model.Shop;
import com.godfathercapybara.capybara.service.CommentService;
import com.godfathercapybara.capybara.service.ImageService;
import com.godfathercapybara.capybara.service.ProductService;
import com.godfathercapybara.capybara.service.ShopService;

@RequestMapping("/api")
@RestController
public class ProductsShopsCommentsAPIController {
	@Autowired
	private ShopService shopService;
	@Autowired
	private ProductService productService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private ImageService imageService;

	@JsonView(Product.Basic.class)
	@GetMapping("/products/")
	public List<Product> getProducts() {
		List<Product> products = productService.findAll();
		return products;
	}

	@JsonView(Shop.Basic.class)
	@GetMapping("/shops/")
	public List<Shop> getShops() {
		List<Shop> shops = shopService.findAll();
		return shops;
	}

	interface ProductDetail extends Product.Basic, Product.Comments, Product.Shops, Shop.Basic, Comment.Basic {
	}

	@JsonView(ProductDetail.class)
	@GetMapping("/products/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable long id) {
		Optional<Product> productOptional = productService.findById(id);

		if (productOptional.isPresent()) {
			Product product = productOptional.get();
			return ResponseEntity.ok(product);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@JsonView(ProductDetail.class)
	@DeleteMapping("/products/{id}")
	public ResponseEntity<Product> deleteProduct(@PathVariable long id) {
		Optional<Product> productOptional = productService.findById(id);

		if (productOptional.isPresent()) {
			Product product = productOptional.get();
			imageService.deleteImage(product.getImage());
			productService.delete(product.getId());
			return ResponseEntity.ok(product);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@PostMapping("/products/")
	public ResponseEntity<Product> createProduct(@RequestBody Product product, MultipartFile imageField) {
		productService.save(product, imageField);
		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(product.getId()).toUri();

		return ResponseEntity.created(location).body(product);
	}

	@PostMapping("/products/{id}/image")
	public ResponseEntity<Object> uploadImage(@PathVariable long id, @RequestParam MultipartFile image)
			throws IOException {
		Optional<Product> productOptional = productService.findById(id);

		if (productOptional.isPresent()) {
			Product product = productOptional.get();
			String path = imageService.createImage(image);
			product.setImage(path);
			productService.updateProduct(product, id, null);

			URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
			return ResponseEntity.created(location).build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/products/{id}/image")
	public ResponseEntity<Object> deleteImage(@PathVariable long id) throws IOException {
		Optional<Product> productOptional = productService.findById(id);

		if (productOptional.isPresent()) {
			Product product = productOptional.get();
			imageService.deleteImage(product.getImage());
			product.setImage(null);
			productService.updateProduct(product, id, null);
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/products/{id}/comments/")
	public ResponseEntity<Comment> createCommentForProduct(@PathVariable long id, @RequestBody Comment comment) {
		Optional<Product> productOptional = productService.findById(id);

		if (productOptional.isPresent()) {
			Product product = productOptional.get();
			commentService.save(comment);
			product.addComment(comment);
			productService.updateProduct(product, id, null);
			URI location = fromCurrentRequest().path("/{id}").buildAndExpand(comment.getId()).toUri();
			return ResponseEntity.created(location).body(comment);

		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/products/{id}/comments/{commentId}")
	public ResponseEntity<Comment> deleteCommentForProduct(@PathVariable long id, @PathVariable long commentId) {
		Optional<Product> productOptional = productService.findById(id);
		if (!productOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Product product = productOptional.get();
		Optional<Comment> optionalComment = commentService.findById(commentId);
		if (!optionalComment.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Comment comment = optionalComment.get();
		if (!product.getComments().contains(comment)) {
			return ResponseEntity.badRequest().build();
		}
		product.removeComment(comment);
		productService.updateProduct(product, id, null);
		commentService.delete(commentId);

		return ResponseEntity.ok(comment);
	}

	interface ShopDetail extends Shop.Basic, Shop.Products, Product.Basic {
	}

	@JsonView(ShopDetail.class)
	@GetMapping("/shops/{id}")
	public ResponseEntity<Shop> getShopById(@PathVariable long id) {

		Optional<Shop> shopOptional = shopService.findById(id);

		if (shopOptional.isPresent()) {
			Shop shop = shopOptional.get();
			return ResponseEntity.ok(shop);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@JsonView(ShopDetail.class)
	@DeleteMapping("/shops/{id}")
	public ResponseEntity<Shop> deleteShop(@PathVariable long id) {
		Optional<Shop> shopOptional = shopService.findById(id);
		if (shopOptional.isPresent()) {
			Shop shop = shopOptional.get();
			shopService.delete(shop.getId());
			return ResponseEntity.ok(shop);

		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@PostMapping("/shops/")
	public ResponseEntity<Shop> createShop(@RequestBody Shop shop) {
		shopService.save(shop);
		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(shop.getId()).toUri();

		return ResponseEntity.created(location).body(shop);
	}

}
