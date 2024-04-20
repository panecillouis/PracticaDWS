package com.godfathercapybara.capybara.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.godfathercapybara.capybara.model.User;
import com.godfathercapybara.capybara.service.UserService;
import com.godfathercapybara.capybara.service.ValidateService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class UserWebController {

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private ValidateService validateService;

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String showHome() {
		return "index";
	}

	@RequestMapping("/login")
	public String login() {
		return "login";
	}      

	@RequestMapping("/loginerror")
	public String loginerror() {
		return "loginerror";
	}
	@GetMapping("/signup")
	public String register() {
		return "signup";
	}
	@PostMapping("/signup")
	public String processRegister(Model model, @ModelAttribute User user, String confirmPassword) {
		if(validateService.validateUser(user, confirmPassword)!=null) {
			model.addAttribute("error", validateService.validateUser(user, confirmPassword));
			model.addAttribute("user", user);
			return "signup";
		}
		else{
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			userService.save(user);
			String success="Usuario "+ user.getUsername() +" registrado con éxito.";
			model.addAttribute("user",user);
			model.addAttribute("success",success);
			return "private";
		}
	}
	@GetMapping("/users")
	public String showUsers(Model model) {
		model.addAttribute("users", userService.findAll());
		return "users";
	}
	@GetMapping("/users/{id}")
	public String showUser(Model model, @PathVariable long id) {
		Optional <User> user = userService.findById(id);
		if(user.isPresent()) {
			model.addAttribute("user", user.get());
			return "private";
		}
		else{
			return "redirect:/";
		}
		
	}
	
	
	@GetMapping("/user/home")
	public String privatePage( Model model) {

		return "private";
	}
	@GetMapping("/users/{id}/delete")
	public String deleteUser(Model model, @PathVariable long id) {
		Optional <User> user = userService.findById(id);
		if(user.isPresent()) {
			userService.delete(id);
		}
		model.addAttribute("name", user.get().getUsername());
		return "removedUser";
	}
	@GetMapping("/users/{id}/edit")
	public String showEditUserForm(@PathVariable("id") long id, Model model) {
		User user = userService.findUserById(id);
		model.addAttribute("user", user);
		return "editPrivatePage";
	}
	@PostMapping("/users/{id}/edit")
	public String processEditUserForm(Model model, @PathVariable("id") long id, @ModelAttribute User updatedUser, String confirmPassword) throws IOException {
		if(validateService.validateUpdatedUser(updatedUser, confirmPassword)!=null) {
			model.addAttribute("error", validateService.validateUpdatedUser(updatedUser, confirmPassword));
			return "editPrivatePage";
		}
		userService.updateUser(updatedUser, id);

		return "redirect:/";
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
