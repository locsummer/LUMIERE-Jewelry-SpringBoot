package com.agile.jewelryshop.repository;

import com.agile.jewelryshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findTop8ByAvailableTrueAndFeaturedTrueOrderByIdDesc();
    Optional<Product> findBySlugAndAvailableTrue(String slug);
    Optional<Product> findBySlug(String slug);
    boolean existsBySlug(String slug);
    long countByCategoryId(Long categoryId);

    @Query("""
        select f from Product f join f.category c
        where f.available = true and c.active = true
          and (:category is null or c.slug = :category)
          and (:keyword is null or lower(f.name) like lower(concat('%', :keyword, '%'))
               or lower(f.description) like lower(concat('%', :keyword, '%')))
        order by f.featured desc, f.id desc
        """)
    List<Product> searchAvailable(@Param("category") String category, @Param("keyword") String keyword);
}
