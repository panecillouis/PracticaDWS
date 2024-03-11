package com.godfathercapybara.capybara.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.godfathercapybara.capybara.model.Capybara;
import com.godfathercapybara.capybara.model.Comment;
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
        @Autowired
        private CommentService commentService;

        @PostConstruct
        public void init() throws IOException {

                // Create some shops
                Shop shop1 = new Shop("CapibaraSuplementos",
                                "Monterrey 47-1er piso, Roma Nte., Cuauhtémoc, 06700 Ciudad de México, CDMX, México");
                Shop shop2 = new Shop("CapibaraSuplementosMadrid", "C. de Pelayo, 46, Centro, 28004 Madrid");

                // Create some capybaras
                Capybara Lola = new Capybara("Hembra", "Blanco", 450,
                                "Tiene problemas de pulmón.", "Lola Lolita", true);
                Lola.setImage("Lola.jpg");

                Capybara Fernanda = new Capybara("Hembra", "Verdoso", 1000, "Es una especie rarísima de Ompabara",
                                "Fernanda");
                Fernanda.setImage("Fernanda.jpg");
                Product pistola = new Product("Pistola", "Arma básica en defensa de capibaras", "Arma", 3092);
                pistola.setImage("pistola.jpg");
                Product camiseta = new Product("Camiseta", "Camiseta para tu capibara", "Ropa", 15);
                camiseta.setImage("camiseta.jpg");
                Comment comment1 = new Comment(
                                "Quería compartir mi experiencia con la pistola que compré hace unos meses. He sido un aficionado a las armas de fuego durante muchos años y he probado diferentes marcas y modelos, pero esta pistola realmente me ha impresionado.",
                                "Juan Pérez");
                Comment comment2 = new Comment(
                                "Se la compré a mi capibara y le ha quedado genial. La camiseta es muy bonita y de buena calidad. La talla es la que esperaba y el envío fue rápido. Muy contenta con la compra.",
                                "María López");
                Comment comment3 = new Comment("Era un regalo para mi hijo y le ha encantado. Juega mucho con ella ",
                                "Ana García");

                pistola.setComments(new ArrayList<>(Arrays.asList(comment1, comment3)));
                camiseta.setComments(new ArrayList<>(Arrays.asList(comment2)));
                pistola.setShops(new ArrayList<>(Arrays.asList(shop1)));
                camiseta.setShops(new ArrayList<>(Arrays.asList(shop2)));

                shop1.getProducts().add(pistola);
                shop2.getProducts().add(camiseta);

                // Save them to the 'Fake' database
                capybaraService.save(Lola, null);
                capybaraService.save(Fernanda, null);
                productService.save(pistola, null);
                productService.save(camiseta, null);
                shopService.save(shop1);
                shopService.save(shop2);
                commentService.save(comment1);
                commentService.save(comment2);
                commentService.save(comment3);

        }

}
