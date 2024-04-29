package com.godfathercapybara.capybara.service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.godfathercapybara.capybara.model.Capybara;
import com.godfathercapybara.capybara.model.Comment;
import com.godfathercapybara.capybara.model.Product;
import com.godfathercapybara.capybara.model.Shop;
import com.godfathercapybara.capybara.model.User;

@Service
public class ValidateService {
    @Autowired
    private UserService userService;

    public String validatePrice(String priceStr) {

        if (priceStr.isEmpty()) {
            return "El campo precio no puede ser nulo";
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                return "El precio debe ser un número mayor o igual a 0";
            }
        } catch (NumberFormatException e) {
            return "El precio debe ser un número válido";
        }
        return null; // Precio válido
    }

    public String validateSex(String sex) {
        if (sex.isEmpty()) {
            return "El campo sexo no puede ser nulo";
        } else if (!Arrays.asList("Hembra", "Macho", "No binario").contains(sex)) {
            return "El sexo debe ser Hembra, Macho o No binario";
        }

        return null;
    }
    public String validateUsername(String username) {
        if (username.isEmpty()) {
            return "El campo nombre de usuario no puede ser nulo";
        }
        if(userService.existsByUsername(username)) {
            return "El nombre de usuario ya existe";
        }

        return null;
    }
   

    public String validateName(String name) {
        if (name.isEmpty()) {
            return "El campo nombre no puede ser nulo";
        }

        return null;
    }
    public String validatePassword(String password , String confirmPassword) {
        if (password.isEmpty()) {
            return "El campo contraseña no puede ser nulo";
        }
        if(password.length()<8) {
            return "La contraseña debe tener al menos 8 caracteres";
        }
        if(!password.equals(confirmPassword)) {
            return "Las contraseñas no coinciden";
        }

        return null;
    }
    public String validateDescription(String description) {
        if (description.isEmpty()) {
            return "El campo descripción no puede ser nula";
        }

        return null;
    }

    public String validateImage(MultipartFile imageField) {
        if (imageField.isEmpty()) {
            return "La imagen no puede ser nula";
        }
        if (!Objects.requireNonNull(imageField.getContentType()).startsWith("image/")) {
            return "El archivo debe ser una imagen";
        }
        return null; // Image valid
    }
    public String validateAnalytics(MultipartFile analyticsField) {
        if (analyticsField.isEmpty()) {
            return "La analitica no puede ser nula";
        }
        if (!Objects.requireNonNull(analyticsField.getContentType()).startsWith("analytics/")) {
            return "El archivo debe ser un pdf";
        }
        return null; // Analytic valid
    }

    public String validateColor(String color) {
        if (color.isEmpty()) {
            return "El campo color no puede ser nulo";
        }

        return null;
    }

    public String validatetype(String type) {
        if (type.isEmpty()) {
            return "El campo tipo no puede ser nulo";
        }
        if (!(type.equals("Arma") || type.equals("Ropa") || type.equals("Juguete"))) {
            return "El tipo debe ser Arma, Ropa o Juguete";
        }
        return null;
    }

    public String validateAddress(String address) {
        if (address.isEmpty()) {
            return "El campo dirección no puede ser nula";
        }

        return null;
    }

    public String validateAuthor(String author) {
        if (author.isEmpty()) {
            return "El campo autor no puede ser nulo";
        }

        return null;
    }
    public String validateEmail(String email) {
        if (email.isEmpty()) {
            return "El campo email no puede ser nulo";
        }
        if(userService.existsByEmail(email)) {
            return "El email ya existe";
        }

        return null;
    }
    public String validateLastName(String lastName) {
        if (lastName.isEmpty()) {
            return "El campo apellido no puede ser nulo";
        }

        return null;
    }

    public String validateText(String text) {
        if (text.isEmpty()) {
            return "El campo comentario no puede ser nulo";
        }

        return null;
    }

    public String validateShop(Shop shop) {
        String addressError = validateAddress(shop.getAddress());
        if (addressError != null) {
            return addressError;
        }
        String nameError = validateName(shop.getName());
        if (nameError != null) {
            return nameError;
        }
        return null; // Tienda válida
    }

    public String validateProduct(Product product, MultipartFile imageField) {
        String priceError = validatePrice(String.valueOf(product.getPrice()));
        if (priceError != null) {
            return priceError;
        }
        String typeError = validatetype(product.getType());
        if (typeError != null) {
            return typeError;
        }
        String nameError = validateName(product.getName());
        if (nameError != null) {
            return nameError;
        }
        String descriptionError = validateDescription(product.getDescription());
        if (descriptionError != null) {
            return descriptionError;
        }
        /*/ 
        String imageError = validateImage(imageField);
        if (imageError != null) {
            return imageError;
        }
         /*/
        return null; // Producto válido
    }

    public String validateComment(Comment comment) {
        String authorError = validateAuthor(comment.getAuthor());
        if (authorError != null) {
            return authorError;
        }
        String textError = validateText(comment.getText());
        if (textError != null) {
            return textError;
        }
        return null; // Comentario válido
    }

    public String validateCapybara(Capybara capybara, MultipartFile imageField, MultipartFile analyticsField) {
        String priceError = validatePrice(String.valueOf(capybara.getPrice()));
        if (priceError != null) {
            return priceError;
        }
        String sexError = validateSex(capybara.getSex());
        if (sexError != null) {
            return sexError;
        }
        String nameError = validateName(capybara.getName());
        if (nameError != null) {
            return nameError;
        }
        String descriptionError = validateDescription(capybara.getDescription());
        if (descriptionError != null) {
            return descriptionError;
        }
        /*/
        String imageError = validateImage(imageField);
        if (imageError != null) {
            return imageError;
        }
        /*/
        /*/
        String analyticsError = validateAnalytics(analyticsField);
        if (analyticsError != null) {
            return analyticsError;
        }
        /*/
        String colorError = validateColor(capybara.getColor());
        if (colorError != null) {
            return colorError;
        }
        return null; // Capybara is valid
    }

    public String validateUpdatedCapybara(Capybara capybara) {
        String priceError = validatePrice(String.valueOf(capybara.getPrice()));
        if (priceError != null) {
            return priceError;
        }
        String sexError = validateSex(capybara.getSex());
        if (sexError != null) {
            return sexError;
        }
        String nameError = validateName(capybara.getName());
        if (nameError != null) {
            return nameError;
        }
        String descriptionError = validateDescription(capybara.getDescription());
        if (descriptionError != null) {
            return descriptionError;
        }

        String colorError = validateColor(capybara.getColor());
        if (colorError != null) {
            return colorError;
        }
        return null; // Capybara is valid
    }
    public String validateUser(User user, String confirmPassword) {
        String usernameError = validateUsername(user.getUsername());
        if (usernameError != null) {
            return usernameError;
        }
       
        String passwordError = validatePassword(user.getPassword(), confirmPassword);
        if (passwordError != null) {
            return passwordError;
        }
        String emailError = validateEmail(user.getEmail());
        if (emailError != null) {
            return emailError;
        }
        return null; // User is valid
    }
    public String validateUpdatedUser(User user, String confirmPassword) {
      
        String nameError = validateName(user.getName());
        if (nameError != null) {
            return nameError;
        }
        String lastNameError =validateLastName(user.getLastName());
        if (lastNameError != null) {
            return lastNameError;
        }
       
        if(!user.getPassword().isEmpty()) {
            String passwordError = validatePassword(user.getPassword(), confirmPassword);
            if (passwordError != null) {
                return passwordError;
            }
        }

        return null; // User is valid
    }
}
