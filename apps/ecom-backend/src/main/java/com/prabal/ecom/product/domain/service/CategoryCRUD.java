package com.prabal.ecom.product.domain.service;

import com.prabal.ecom.product.domain.aggregate.Category;
import com.prabal.ecom.product.domain.repository.CategoryRepository;
import com.prabal.ecom.product.domain.vo.PublicId;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class CategoryCRUD {

  private final CategoryRepository categoryRepository;

  public CategoryCRUD(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public Category save(Category category) {
    category.initDefaultFields();
    return categoryRepository.save(category);
  }

  public Page<Category> findAll(Pageable pageable) {
    return categoryRepository.findAll(pageable);
  }

  public PublicId delete(PublicId categoryId) {
    int deleted = categoryRepository.delete(categoryId);
    if (deleted != 1) {
      throw new EntityNotFoundException(String.format("No category deleted with id %s", categoryId));
    }
    return categoryId;
  }

  public Optional<Category> findFeaturedCategory(String product) {
    return categoryRepository.findFeaturedCategory(product);
  }
}
