package com.godfathercapybara.capybara.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.godfathercapybara.capybara.model.Shop;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    
}
