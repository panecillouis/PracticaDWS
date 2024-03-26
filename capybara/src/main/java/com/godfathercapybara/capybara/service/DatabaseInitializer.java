package com.godfathercapybara.capybara.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.godfathercapybara.capybara.model.Capybara;
import com.godfathercapybara.capybara.model.Comment;
import com.godfathercapybara.capybara.model.Product;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {

        @Autowired
        private ProductService productService;

       
        @Autowired
        private CapybaraService capybaraService;
        @Autowired
        private CommentService commentService;

        @PostConstruct
        public void init() throws IOException {

                // Create some capybaras
                Capybara Lola = new Capybara("Hembra", "Blanco", 450,
                                "Tiene problemas de pulmón.", "Lola Lolita", true);
                Lola.setImage("Lola.jpg");
                Lola.setImageFile(BlobProxy.generateProxy(Files.readAllBytes(Paths.get("images/Lola.jpg"))));
                Lola.setAnalytics("analytics_of_Lola.pdf");
                Capybara Fernanda = new Capybara("Hembra", "Verdoso", 1000, "Es una especie rarísima de Ompabara",
                                "Fernanda");
                Fernanda.setImage("Fernanda.jpg");
                Fernanda.setImageFile(BlobProxy.generateProxy(Files.readAllBytes(Paths.get("images/Fernanda.jpg"))));
                Fernanda.setAnalytics("analytics_of_Fernanda.pdf");
                Product pistola = new Product("Pistola", "Arma básica en defensa de capibaras", "Arma", 3092);
                pistola.setImage("pistola.jpg");
                pistola.setImageFile(BlobProxy.generateProxy(Files.readAllBytes(Paths.get("images/pistola.jpg"))));
                Product camiseta = new Product("Camiseta", "Camiseta para tu capibara", "Ropa", 15);
                camiseta.setImage("camiseta.jpg");
                camiseta.setImageFile(BlobProxy.generateProxy(Files.readAllBytes(Paths.get("images/camiseta.jpg"))));
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


                // Save them to the 'Fake' database
                capybaraService.save(Lola, null, null);
                capybaraService.save(Fernanda, null, null);
                
                commentService.save(comment1);
                commentService.save(comment2);
                commentService.save(comment3);

                productService.save(pistola, null);
                productService.save(camiseta, null);

                

        }

}
