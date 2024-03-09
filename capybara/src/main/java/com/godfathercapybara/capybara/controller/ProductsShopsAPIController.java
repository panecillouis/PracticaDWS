package com.godfathercapybara.capybara.controller;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.*;

import java.io.IOException;
import java.net.URI;
import java.util.List;

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
public class ProductsShopsAPIController {
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

	interface ProductDetail extends Product.Basic, Product.Comments, Product.Shops, Shop.Basic {
	}

	@JsonView(ProductDetail.class)
	@GetMapping("/products/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable long id) {
		List<Product> products = productService.findAll();
		int productId = Integer.parseInt(String.valueOf(id));
		Product product = products.get(productId);
		return ResponseEntity.ok(product);
	}
	@JsonView(ProductDetail.class)
	@DeleteMapping("/products/{id}")
	public ResponseEntity<Product> deleteProduct(@PathVariable long id) {
		List<Product> products = productService.findAll();
		int productId = Integer.parseInt(String.valueOf(id));
		Product product = products.get(productId);
		productService.delete(product.getId());
		return ResponseEntity.ok(product);
		
	}
	@PostMapping("/products/")
	public ResponseEntity<Product> createProduct(@RequestBody Product product, MultipartFile imageField) {
         productService.save(product, imageField);
         URI location = fromCurrentRequest().path("/{id}").buildAndExpand(product.getId()).toUri();

         return ResponseEntity.created(location).body(product);
    }
	@PostMapping("/products/{id}/image")
    public ResponseEntity<Object> uploadImage(@PathVariable long id, @RequestParam MultipartFile image) throws IOException {

		List<Product> products = productService.findAll();
		int productId = Integer.parseInt(String.valueOf(id));
		Product product = products.get(productId);
        if (product != null) {
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
		List<Product> products = productService.findAll();
		int productId = Integer.parseInt(String.valueOf(id));
		Product product = products.get(productId);
        if (product != null) {
            imageService.deleteImage(product.getImage());
            product.setImage(null);
            productService.updateProduct(product, productId, null);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
	

	@GetMapping("/products/{id}/comments")
	public ResponseEntity<List<Comment>> getCommentsForProduct(@PathVariable long id) {
		List<Product> products = productService.findAll();
		int productId = Integer.parseInt(String.valueOf(id));
		Product product = products.get(productId);
		if (product != null) {
            List<Comment> comments = product.getComments();
            return ResponseEntity.ok(comments);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
	@PostMapping("/products/{id}/comments")
    public ResponseEntity<Comment> createCommentForProduct(@PathVariable long id, @RequestBody Comment comment) {
        List<Product> products = productService.findAll();
		int productId = Integer.parseInt(String.valueOf(id));
		Product product = products.get(productId);
		if (product != null) {
			productService.addComment(productId, comment);
			commentService.save(comment);
            return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
	@DeleteMapping("/products/{id}/comments/{idComment}/delete")
	public ResponseEntity<Comment> deleteCommentForProduct(@PathVariable long id, @PathVariable long idComment) {
		List<Comment> comments = commentService.findAll();
			int commentId = Integer.parseInt(String.valueOf(idComment));
			Comment comment = comments.get(commentId);
        if (comment!= null) {
			productService.deleteComment(id, idComment);
			commentService.delete(comment.getId());
			return ResponseEntity.ok(comment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

	interface ShopDetail extends Shop.Basic, Shop.Products, Product.Basic {
	}

	@JsonView(ShopDetail.class)
	@GetMapping("/shops/{id}")
	public ResponseEntity<Shop> getShopById(@PathVariable long id) {
		List<Shop> shops = shopService.findAll();
		int shopId = Integer.parseInt(String.valueOf(id));
		Shop shop = shops.get(shopId);
		return ResponseEntity.ok(shop);
	}
	
	@JsonView(ShopDetail.class)
	@DeleteMapping("/shops/{id}")
	public ResponseEntity<Shop> deleteShop(@PathVariable long id) {
        List<Shop> shops = shopService.findAll();
		int shopId = Integer.parseInt(String.valueOf(id));
		Shop shop = shops.get(shopId);
        shopService.delete(shop.getId());
        return ResponseEntity.ok(shop);
        
    }

	@PostMapping("/shops/")
	public ResponseEntity<Shop> createShop(@RequestBody Shop shop) {
		shopService.save(shop);
		URI location = fromCurrentRequest().path("/{id}").buildAndExpand(shop.getId()).toUri();

		return ResponseEntity.created(location).body(shop);
	}
	



}
