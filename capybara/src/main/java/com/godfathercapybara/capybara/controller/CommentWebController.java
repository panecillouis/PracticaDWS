package com.godfathercapybara.capybara.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.godfathercapybara.capybara.model.Comment;
import com.godfathercapybara.capybara.model.Product;
import com.godfathercapybara.capybara.model.User;
import com.godfathercapybara.capybara.service.CommentService;
import com.godfathercapybara.capybara.service.ProductService;
import com.godfathercapybara.capybara.service.UserService;
import com.godfathercapybara.capybara.service.ValidateService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CommentWebController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ValidateService validateService;
    @Autowired
    private UserService userService;

    @GetMapping("/products/{id}/newcomment")
    public String newComment(Model model, @PathVariable long id) {

        model.addAttribute("product", productService.findById(id).get());

        return "newCommentPage";
    }

    @PostMapping("/products/{id}/newcomment")
    public String newCommentProcess(Model model, @PathVariable long id, Comment comment, HttpServletRequest request)
            throws IOException {

        comment.setText(Jsoup.clean(comment.getText(), Safelist.relaxed()));
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return "redirect:/login";
        } else {
            String name = principal.getName();
            comment.setAuthor(name);
            if (validateService.validateComment(comment) != null) {
                model.addAttribute("error", validateService.validateComment(comment));
                model.addAttribute("comment", comment);
                model.addAttribute("product", productService.findById(id).get());
                return "newCommentPage";
            } else {
                Optional<Product> productOptional = productService.findById(id);
                if (productOptional.isPresent()) {
                    commentService.save(comment);
                    productService.addComment(id, comment);

                    return "redirect:/products/" + id;
                } else {
                    return "products";
                }
            }
        }
    }

    @GetMapping("/products/{id}/comments/{idComment}/delete")
    public String deleteComment(Model model, @PathVariable long id, @PathVariable long idComment,
            HttpServletRequest request) throws IOException{
        Optional<Product> productOptional = productService.findById(id);
		if (!productOptional.isPresent()) {
            return "redirect:/products/" + id;
		}
		Product product = productOptional.get();
		Optional<Comment> optionalComment = commentService.findById(idComment);
		if (!optionalComment.isPresent()) {
            return "redirect:/products/" + id;
		}
		Principal principal = request.getUserPrincipal();
		if (principal == null) {
            return "redirect:/products/" + id;
		} else {
			Comment comment = optionalComment.get();
			if (!product.getComments().contains(comment)) {
                return "redirect:/products/" + id;
			}
			String userName = principal.getName();
			if (!commentService.isAuthor(idComment, userName) && !request.isUserInRole("ADMIN")) {
                return "redirect:/products/" + id;
			} else {
				productService.deleteComment(id, idComment);
				productService.updateProduct(product, id, null);
				commentService.delete(idComment);

                return "redirect:/products/" + id;
			}
		}
    }

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();

        if (principal != null) {

            model.addAttribute("logged", true);
            String name = principal.getName();
            Optional<User> userOptional = userService.findByUsername(name);
            User user = userOptional.get();
            model.addAttribute("user", user);
            model.addAttribute("admin", request.isUserInRole("ADMIN"));

        } else {
            model.addAttribute("logged", false);
        }
    }

}
