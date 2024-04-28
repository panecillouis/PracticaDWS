package com.godfathercapybara.capybara.controller;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.*;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.godfathercapybara.capybara.model.User;
import com.godfathercapybara.capybara.security.jwt.AuthResponse;
import com.godfathercapybara.capybara.security.jwt.AuthResponse.Status;
import com.godfathercapybara.capybara.security.jwt.LoginRequest;
import com.godfathercapybara.capybara.security.jwt.UserLoginService;
import com.godfathercapybara.capybara.service.UserService;
import com.godfathercapybara.capybara.service.ValidateService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class UserAPILoginController {

	@Autowired
	private UserLoginService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ValidateService validateService;

	@Autowired
	private UserService userService2;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(
			@CookieValue(name = "accessToken", required = false) String accessToken,
			@CookieValue(name = "refreshToken", required = false) String refreshToken,
			@RequestBody LoginRequest loginRequest) {
		
		return userService.login(loginRequest, accessToken, refreshToken);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(
			@CookieValue(name = "refreshToken", required = false) String refreshToken) {

		return userService.refresh(refreshToken);
	}

	@PostMapping("/logout")
	public ResponseEntity<AuthResponse> logOut(HttpServletRequest request, HttpServletResponse response) {

		return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userService.logout(request, response)));
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
            userService2.save(user);
            URI location = fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
            return ResponseEntity.created(location).body(user);
        }
    }


}
