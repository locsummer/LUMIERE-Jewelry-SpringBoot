package com.agile.jewelryshop.repository;

import com.agile.jewelryshop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByActiveTrueOrderByNameAsc();
    Optional<Category> findBySlug(String slug);
    boolean existsByNameIgnoreCase(String name);
}
