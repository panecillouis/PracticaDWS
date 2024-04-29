package com.godfathercapybara.capybara.controller;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.*;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.engine.jdbc.BlobProxy;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.annotation.JsonView;
import com.godfathercapybara.capybara.model.Comment;
import com.godfathercapybara.capybara.model.Product;
import com.godfathercapybara.capybara.model.Shop;
import com.godfathercapybara.capybara.service.CommentService;
import com.godfathercapybara.capybara.service.ProductService;
import com.godfathercapybara.capybara.service.ShopService;
import com.godfathercapybara.capybara.service.ValidateService;

import jakarta.servlet.http.HttpServletRequest;

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
	private ValidateService validateService;

	@JsonView(Product.Basic.class)
	@GetMapping("/products")
	public List<Product> getProducts(@RequestParam(required = false) Boolean comment,
			@RequestParam(required = false) Double price, @RequestParam(required = false) String type) {
		List<Product> products = productService.findAll(comment, price, type);
		return products;
	}

	@JsonView(Shop.Basic.class)
	@GetMapping("/shops")
	public List<Shop> getShops(@RequestParam(required = false) String address) {
		List<Shop> shops = shopService.findAll(address);
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
			List<Shop> shops = product.getShops();
			for (Shop shop : shops) {
				shop.getProducts().remove(product);
			}
			productService.delete(product.getId());
			return ResponseEntity.ok(product);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@PostMapping("/products")
	public ResponseEntity<?> createProduct(@RequestBody Product product, MultipartFile imageField,
			@RequestParam(required = false) List<Long> selectedShops) throws IOException {

		String error = validateService.validateProduct(product, imageField);
		if (error != null) {
			Map<String, Object> response = new HashMap<>();
			response.put("error", error);
			response.put("product", product);
			return ResponseEntity.badRequest().body(response);
		}

		else {
			if (selectedShops != null) {
				List<Shop> shops = shopService.findByIds(selectedShops);
				product.setShops(shops);
				for (Shop shop : shops) {
					shop.getProducts().add(product);
				}
			}

			productService.save(product, imageField);

			URI location = fromCurrentRequest().path("/{id}").buildAndExpand(product.getId()).toUri();

			return ResponseEntity.created(location).body(product);
		}
	}

	@PostMapping("/products/{id}/image")
	public ResponseEntity<Object> uploadImage(@PathVariable long id, @RequestParam MultipartFile image)
			throws IOException {
		Optional<Product> productOptional = productService.findById(id);

		Product product = productOptional.get();
		URI location = fromCurrentRequest().build().toUri();
		product.setImage(location.toString());
		product.setImageFile(BlobProxy.generateProxy(image.getInputStream(), image.getSize()));
		productService.updateProduct(product, id, image);
		return ResponseEntity.created(location).build();

	}

	@SuppressWarnings("null")
	@GetMapping("products/{id}/image")
	public ResponseEntity<Resource> downloadImage(@PathVariable long id) {
		try {
			Product product = productService.findById(id).orElseThrow();
			if (product.getImageFile() == null) {
				return ResponseEntity.notFound().build();
			}

			Resource file = new InputStreamResource(product.getImageFile().getBinaryStream());
			String mimeType = MimeTypeUtils.IMAGE_JPEG_VALUE;

			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + product.getImage() + "\"")
					.contentType(MediaType.parseMediaType(mimeType))
					.contentLength(product.getImageFile().length())
					.body(file);

		} catch (SQLException ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Can't retrieve capybara image", ex);
		}
	}

	@DeleteMapping("/products/{id}/image")
	public ResponseEntity<Object> deleteImage(@PathVariable long id) throws IOException {
		Optional<Product> productOptional = productService.findById(id);
		Product product = productOptional.get();
		product.setImage("no-image.png");
		product.setImageFile(null);
		productService.updateProduct(product, id, null);
		return ResponseEntity.noContent().build();

	}

	@PostMapping("/products/{id}/comments")
	public ResponseEntity<?> createCommentForProduct(@PathVariable long id, @RequestBody Comment comment,
			HttpServletRequest request)
			throws IOException {
		comment.setText(Jsoup.clean(comment.getText(), Safelist.relaxed()));
		Principal principal = request.getUserPrincipal();
		if (principal == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} else {
			String name = principal.getName();
			comment.setAuthor(name);
			Optional<Product> productOptional = productService.findById(id);
			String error = validateService.validateComment(comment);
			if (error != null) {
				Map<String, Object> response = new HashMap<>();
				response.put("error", error);
				response.put("comment", comment);
				return ResponseEntity.badRequest().body(response);
			} else {
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
		}
	}

	@DeleteMapping("/products/{id}/comments/{commentId}")
	public ResponseEntity<Comment> deleteCommentForProduct(@PathVariable long id, @PathVariable long commentId,
			HttpServletRequest request)
			throws IOException {
		Optional<Product> productOptional = productService.findById(id);
		if (!productOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Product product = productOptional.get();
		Optional<Comment> optionalComment = commentService.findById(commentId);
		if (!optionalComment.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		Principal principal = request.getUserPrincipal();
		if (principal == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} else {
			Comment comment = optionalComment.get();
			if (!product.getComments().contains(comment)) {
				return ResponseEntity.badRequest().build();
			}
			String userName = principal.getName();
			if (!commentService.isAuthor(commentId, userName) && !request.isUserInRole("ADMIN")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			} else {
				productService.deleteComment(id, commentId);
				productService.updateProduct(product, id, null);
				commentService.delete(commentId);

				return ResponseEntity.ok(comment);
			}
		}
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
	public ResponseEntity<Shop> deleteShop(@PathVariable long id) throws IOException {
		Optional<Shop> shopOptional = shopService.findById(id);
		if (shopOptional.isPresent()) {
			Shop shop = shopOptional.get();
			List<Product> products = shop.getProducts();
			for (Product product : products) {
				product.getShops().remove(shop);
			}
			shopService.delete(id);
			return ResponseEntity.ok(shop);

		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@PostMapping("/shops")
	public ResponseEntity<?> createShop(@RequestBody Shop shop,
			@RequestParam(required = false) List<Long> selectedProducts) throws IOException {

		String error = validateService.validateShop(shop);
		if (error != null) {
			Map<String, Object> response = new HashMap<>();
			response.put("error", error);
			response.put("shop", shop);
			return ResponseEntity.badRequest().body(response);
		} else {
			if (selectedProducts != null) {
				List<Product> products = productService.findByIds(selectedProducts);
				shop.setProducts(products);
				for (Product product : products) {
					product.getShops().add(shop);
				}
			}
			shopService.save(shop);
			URI location = fromCurrentRequest().path("/{id}").buildAndExpand(shop.getId()).toUri();
			return ResponseEntity.created(location).body(shop);
		}
	}

}
