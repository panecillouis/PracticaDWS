package com.godfathercapybara.capybara.controller;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.*;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.godfathercapybara.capybara.model.User;
import com.godfathercapybara.capybara.service.UserService;
import com.godfathercapybara.capybara.service.ValidateService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/users")
public class UserAPIController {
	
	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ValidateService validateService;

	@GetMapping("")
    public List<User> getAllUsers() {

        return userService.findAll();
    }
	@GetMapping("/me")
	public ResponseEntity<User> me(HttpServletRequest request) {
		
		Principal principal = request.getUserPrincipal();
		
		if(principal != null) {
			return ResponseEntity.ok(userService.findByUsername(principal.getName()).orElseThrow());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody User user) throws IOException {
        String confirmPassword = user.getPassword();
		String error = validateService.validateUser(user, confirmPassword);
        if (error != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", error);
            response.put("user", user);
            return ResponseEntity.badRequest().body(response);
        } else {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.save(user);
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
            return ResponseEntity.created(location).body(user);
        }
    }
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable long id, @RequestBody User newuser, HttpServletRequest request) throws IOException {
    Principal principal = request.getUserPrincipal();
    String confirmPassword = newuser.getPassword();
    String error = validateService.validateUpdatedUser(newuser, confirmPassword);
		if (error == null && principal!= null) {
			String name = principal.getName();
			Optional<User> userOptional = userService.findByUsername(name);
			if (userOptional.isPresent()) {
				User userLogged = userOptional.get();
				if (userService.isUser(userLogged.getId(), id) || request.isUserInRole("ADMIN")) {
					newuser.setId(id);
					userService.updateUser(newuser, id);
					return ResponseEntity.ok(newuser);
				} else {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
				}
			} else {
				return ResponseEntity.notFound().build();
			}
		} else if (error!= null) {
			Map<String, Object> response = new HashMap<>();
			response.put("error", error);
			response.put("user", newuser);
			return ResponseEntity.badRequest().body(response);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable long id, HttpServletRequest request) {
		Optional<User> user = userService.findById(id);
		Principal principal = request.getUserPrincipal();
		if (principal!= null) {
			String name = principal.getName();
			Optional<User> userOptional = userService.findByUsername(name);
			if (userOptional.isPresent()) {
				User userLogged = userOptional.get();
				if (userService.isUser(userLogged.getId(), id) || request.isUserInRole("ADMIN")) {
					userService.delete(id);
					return ResponseEntity.ok(user);
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
				}
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable long id, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if(principal !=null)
		{
			String name = principal.getName();
			Optional<User> userOptionalLogged = userService.findByUsername(name);
			User userLogged = userOptionalLogged.get();
			Optional<User> user = userService.findById(id);
			if((userOptionalLogged.isPresent()) && (userService.isUser(userLogged.getId(), id)|| request.isUserInRole("ADMIN"))){
				return ResponseEntity.ok(user.get());
			}
			else{
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

		}
		else{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		
		}
	}
}
