package com.godfathercapybara.capybara.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.godfathercapybara.capybara.model.Shop;
import com.godfathercapybara.capybara.service.ShopService;

@Controller
public class ShopWebController {
    
    @Autowired
    private ShopService shopService;

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
}
