package com.prabal.ecom.product.infrastructure.secondary.repository;

import com.prabal.ecom.product.infrastructure.secondary.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JpaCategoryRepository extends JpaRepository<CategoryEntity, Long> {

  Optional<CategoryEntity> findByPublicId(UUID publicId);

  int deleteByPublicId(UUID publicId);

  //give me query to get the publicId from CategoryEntity based on the name which is product as input parameter
  @Query("SELECT c FROM CategoryEntity c WHERE c.name = :product")
  Optional<CategoryEntity> findFeaturedCategoryFromProduct(String product);
}
