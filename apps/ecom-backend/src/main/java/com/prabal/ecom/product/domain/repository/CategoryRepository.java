package com.prabal.ecom.product.domain.repository;

import com.prabal.ecom.product.domain.aggregate.Category;
import com.prabal.ecom.product.domain.vo.PublicId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface CategoryRepository {

  Page<Category> findAll(Pageable pageable);

  int delete(PublicId publicId);

  Category save(Category categoryToCreate);

  Optional<Category> findFeaturedCategory(String product);
}
