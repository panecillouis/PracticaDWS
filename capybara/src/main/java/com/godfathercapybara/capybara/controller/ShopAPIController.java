package com.godfathercapybara.capybara.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.godfathercapybara.capybara.model.Shop;
import com.godfathercapybara.capybara.service.ShopService;

@RequestMapping("/api/shops")
@RestController
public class ShopAPIController {
    @Autowired
    private ShopService shopService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Shop> deleteShop(@PathVariable("id") long id) {
        Optional<Shop> shopOptional = shopService.findById(id);
        if (shopOptional.isPresent()) {
            Shop shop = shopOptional.get();
            shopService.delete(shop.getId());
            return ResponseEntity.ok(shop);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
     @GetMapping("/")
    public List<Shop> getAllShops() {
       List<Shop> shops = shopService.findAll();
         return shops;
    }
    @GetMapping("/{id}")
	public ResponseEntity<Shop> getShop(@PathVariable long id) {

		Optional <Shop> shop = shopService.findById(id);

		if (shop.isPresent()) {
			return ResponseEntity.ok(shop.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
     @PostMapping("/")
    public ResponseEntity<Shop> createShop(@RequestBody Shop shop) {
         shopService.save(shop);
         return ResponseEntity.ok(shop);
         
    }
    

    
}
