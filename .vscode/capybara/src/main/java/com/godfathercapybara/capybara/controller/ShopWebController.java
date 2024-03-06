package com.godfathercapybara.capybara.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.godfathercapybara.capybara.model.Product;
import com.godfathercapybara.capybara.model.Shop;
import com.godfathercapybara.capybara.service.ShopService;
import com.godfathercapybara.capybara.service.ProductService;
@Controller
public class ShopWebController {
    
    @Autowired
    private ShopService shopService;
	@Autowired
	private ProductService productService;

    @GetMapping("/shops")
	public String showShops(Model model) {

		model.addAttribute("shops", shopService.findAll());

		return "shops";
	}

	@GetMapping("/shops/{id}")
	public String showBook(Model model, @PathVariable long id) {

		Optional<Shop> shop = shopService.findById(id);
		if (shop.isPresent()) {
			model.addAttribute("shop", shop.get());
			return "shop";
		} else {
			return "shops";
		}

	}
	@GetMapping("/shops/{id}/delete")
	public String deleteShop(Model model, @PathVariable long id) {
		Optional <Shop> shop = shopService.findById(id);
		if(shop.isPresent()) {
			shopService.delete(id);
		}
		model.addAttribute("name", shop.get().getName());
			return "removedshop";
		
	}
	@GetMapping("/newshop")
	public String newShop(Model model) {

		model.addAttribute("availableProducts", productService.findAll());

		return "newShopPage";
	}

	@PostMapping("/newshop")
	public String newShopProcess(Model model, Shop shop, @RequestParam(required = false) List<Long> selectedProducts) throws IOException {

		if (selectedProducts != null){
			List<Product> products = productService.findByIds(selectedProducts);
			shop.setProducts(products);
			for (Product product : products){
				product.getShops().add(shop);
			}
		}

		Shop newShop = shopService.save(shop);

		model.addAttribute("shopId", newShop.getId());

		return "redirect:/shops/"+newShop.getId();
	}
	
	
}