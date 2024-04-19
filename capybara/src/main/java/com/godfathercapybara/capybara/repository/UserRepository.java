package com.godfathercapybara.capybara.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.godfathercapybara.capybara.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByName(String name);

}
