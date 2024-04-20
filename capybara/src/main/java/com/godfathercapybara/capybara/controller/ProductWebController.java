package com.godfathercapybara.capybara.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.godfathercapybara.capybara.model.Product;
import com.godfathercapybara.capybara.model.Shop;
import com.godfathercapybara.capybara.model.User;
import com.godfathercapybara.capybara.service.ProductService;
import com.godfathercapybara.capybara.service.ShopService;
import com.godfathercapybara.capybara.service.ValidateService;
import com.godfathercapybara.capybara.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ProductWebController {
	@Autowired
	private ShopService shopService;

	@Autowired
	private ProductService productService;

	@Autowired
	private ValidateService validateService;

	@Autowired
	private UserService userService;

	@GetMapping("/products")
	public String showProducts(Model model, @RequestParam(required = false) Boolean comment, @RequestParam(required = false) Double price, @RequestParam(required = false) String type) {

		model.addAttribute("products", productService.findAll(comment, price, type));

		return "products";
	}

	@GetMapping("/products/{id}")
	public String showProduct(Model model, @PathVariable long id) {

		Optional<Product> product = productService.findById(id);
		if (product.isPresent()) {
			model.addAttribute("product", product.get());
			return "product";
		} else {
			return "redirect:/products";
		}

	}

	/**
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	@GetMapping("/products/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

		Optional<Product> productOptional = productService.findById(id);
		Product product = productOptional.get();
		if (product.getImageFile() != null) {
			@SuppressWarnings("null")
			Resource file = new InputStreamResource(product.getImageFile().getBinaryStream());
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
					.contentLength(product.getImageFile().length())
					.body(file);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@GetMapping("/newproduct")
	public String newProduct(Model model) {

		model.addAttribute("availableShops", shopService.findAll());

		return "newProductPage";
	}

	@PostMapping("/newproduct")
	public String newProductProcess(Model model, Product product, MultipartFile imageField,
			@RequestParam(required = false) List<Long> selectedShops) throws IOException {
		if (validateService.validateProduct(product, imageField) != null) {
			model.addAttribute("error", validateService.validateProduct(product, imageField));
			model.addAttribute("product", product);
			model.addAttribute("availableShops", shopService.findAll());
			return "newProductPage";
		} else {
			if (selectedShops != null) {
				List<Shop> shops = shopService.findByIds(selectedShops);
				product.setShops(shops);
				for(Shop shop : shops) {
					shop.getProducts().add(product);
				}
				
			}

			Product newProduct = productService.save(product, imageField);

			model.addAttribute("productId", newProduct.getId());

			return "redirect:/products/" + newProduct.getId();
		}
	}

	@GetMapping("/products/{id}/delete")
	public String deleteProduct(Model model, @PathVariable long id) {
		Optional<Product> product = productService.findById(id);

		if (product.isPresent()) {
			Product existingProduct = product.get();

			// Delete the product from the shops
			List<Shop> shops = existingProduct.getShops();
			for (Shop shop : shops) {
				shop.getProducts().remove(existingProduct);
			}
			// Delete the product
			productService.delete(id);
		}
		model.addAttribute("name", product.get().getName());

		return "removedProduct";
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
