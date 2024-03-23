package com.godfathercapybara.capybara.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.godfathercapybara.capybara.model.Capybara;

public interface CapybaraRepository extends JpaRepository <Capybara, Long> {
    
}
