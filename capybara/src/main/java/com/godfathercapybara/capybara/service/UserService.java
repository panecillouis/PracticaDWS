package com.godfathercapybara.capybara.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.godfathercapybara.capybara.model.Capybara;
import com.godfathercapybara.capybara.model.User;
import com.godfathercapybara.capybara.repository.UserRepository;
import com.godfathercapybara.capybara.service.CapybaraService;
import jakarta.persistence.EntityManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	private AtomicLong nextId = new AtomicLong(1L);

	public Optional<User> findById(long id) {
		if (this.exist(id)) {
			return Optional.of(this.findUserById(id));
		}
		return Optional.empty();
	}

	public boolean exist(long id) {
		return userRepository.existsById(id);
	}

	public List<User> findAll() {
		return this.userRepository.findAll();
	}

	public boolean existsByUsername(String username) {
		List<User> users = this.userRepository.findAll();
		for (User user : users) {
			if (user.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	public boolean existsByEmail(String email) {
		List<User> users = this.userRepository.findAll();
		for (User user : users) {
			if (user.getEmail()!=null) {
				if (user.getEmail().equals(email)) {
					return true;
				}
			}
		}
		return false;
	}

	public Optional<User> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public User save(User user) {
		long id = nextId.getAndIncrement();
		user.setId(id);
		user.setRoles(List.of("USER"));
		userRepository.save(user);
		return user;
	}
	public User saveAdmin(User user) {
		long id = nextId.getAndIncrement();
		user.setId(id);
		user.setRoles(List.of("ADMIN"));
		userRepository.save(user);
		return user;
	}


	public void delete(long id) {
		User existingUser = this.findUserById(id);
		userRepository.deleteById(id);
	}

	public User findUserById(long id) {
		return userRepository.findById(id).orElseThrow();
	}

	public void updateUser(User user, long id) throws IOException {
		
		if(!userRepository.existsById(id)) {
			throw new IllegalArgumentException("User not found");
		}
		if(user.getPassword()!=null)
		{
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		else{
			user.setPassword(findUserById(id).getPassword());
		}
	
		userRepository.save(user);

	}

	public boolean isAdmin(String username) {
		User user = userRepository.findByUsername(username).orElseThrow();
		return user.getRoles().contains("ADMIN");
	}

	public boolean isUser(String username) {
		User user = userRepository.findByUsername(username).orElseThrow();
		return user.getRoles().contains("USER");
	}
	public void addCapybara(long id, long capybaraId) {
		User user = userRepository.findById(id).orElseThrow();
		Capybara capybara = entityManager.find(Capybara.class, capybaraId);
		user.setCapybara(capybara);
		userRepository.save(user);
	}

}