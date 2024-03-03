package com.godfathercapybara.capybara.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.godfathercapybara.capybara.model.Capybara;
import com.godfathercapybara.capybara.model.Product;
import com.godfathercapybara.capybara.model.Shop;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {

    @Autowired
    private ProductService productService;

    @Autowired
    private ShopService shopService;
    @Autowired
    private CapybaraService capybaraService;

    @PostConstruct
    public void init() throws IOException {

        // Create some shops
        Shop shop1 = new Shop("CapibaraSuplementos", "Monterrey 47-1er piso, Roma Nte., Cuauhtémoc, 06700 Ciudad de México, CDMX, México");
		Shop shop2 = new Shop("CapibaraSuplementosMadrid", "C. de Pelayo, 46, Centro, 28004 Madrid");
        

        // Create some capybaras
        Capybara Lola = new Capybara("Hembra", "Blanco", 450,
                "Tiene problemas de pulmón.", "Lola Lolita");
        Lola.setImage("Lola.jpg");
        
        Capybara Fernanda = new Capybara("Hembra","Verdoso", 1000, "Es una especie rarísima de Ompabara", "Fernanda");
        Fernanda.setImage("Fernanda.jpg");
        Product pistola = new Product ("Pistola", "Arma básica en defensa de capibaras", "Arma", 3092);
        pistola.setImage("pistola.jpg");
        Product camiseta = new Product ("Camiseta", "Camiseta para tu capibara", "Ropa", 15);
        camiseta.setImage("camiseta.jpg");
        // Add shops to the book and the book to the shops
        pistola.setShops(List.of(shop1));
        camiseta.setShops(List.of(shop2));
        shop1.getProducts().add(pistola);
        shop2.getProducts().add(camiseta);



        // Save them to the 'Fake' database
        capybaraService.save(Lola, null);
        capybaraService.save(Fernanda, null);
        productService.save(pistola,null);
        productService.save(camiseta,null);
        shopService.save(shop1);
        shopService.save(shop2);
    }

}
