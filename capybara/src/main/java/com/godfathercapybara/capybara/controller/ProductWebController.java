package com.godfathercapybara.capybara.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.godfathercapybara.capybara.model.Capybara;
import com.godfathercapybara.capybara.model.Product;
import com.godfathercapybara.capybara.model.Shop;
import com.godfathercapybara.capybara.service.ImageService;
import com.godfathercapybara.capybara.service.ProductService;
import com.godfathercapybara.capybara.service.ShopService;
import com.godfathercapybara.capybara.service.ValidateService;

@Controller
public class ProductWebController {
	@Autowired
	private ShopService shopService;

	@Autowired
	private ProductService productService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private ValidateService validateService;

	@GetMapping("/products")
	public String showProducts(Model model) {

		model.addAttribute("products", productService.findAll());

		return "products";
	}

	@GetMapping("/products/{id}")
	public String showProduct(Model model, @PathVariable long id) {

		Optional<Product> product = productService.findById(id);
		if (product.isPresent()) {
			model.addAttribute("product", product.get());
			return "product";
		} else {
			return "products";
		}

	}

	/**
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	@GetMapping("/products/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException {

		Optional<Product> op = productService.findById(id);

		if (op.isPresent()) {
			Product product = op.get();
			Resource image = imageService.getImage(product.getImage());
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(image);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
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
				for (Shop shop : shops) {
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

			// Delete the image
			imageService.deleteImage(existingProduct.getImage());
			// Delete the capybara
			productService.delete(id);
		}
		model.addAttribute("name", product.get().getName());

		return "removedProduct";
	}

}
