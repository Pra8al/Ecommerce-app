package com.prabal.ecom.product.infrastructure.secondary.repository;

import com.prabal.ecom.product.domain.aggregate.Category;
import com.prabal.ecom.product.domain.repository.CategoryRepository;
import com.prabal.ecom.product.domain.vo.PublicId;
import com.prabal.ecom.product.infrastructure.secondary.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SpringDataCategoryRepository implements CategoryRepository {

  private final JpaCategoryRepository jpaCategoryRepository;

  public SpringDataCategoryRepository(JpaCategoryRepository jpaCategoryRepository) {
    this.jpaCategoryRepository = jpaCategoryRepository;
  }

  @Override
  public Page<Category> findAll(Pageable pageable) {
    return jpaCategoryRepository.findAll(pageable).map(CategoryEntity::to);
  }

  @Override
  public int delete(PublicId publicId) {
    return jpaCategoryRepository.deleteByPublicId(publicId.value());
  }

  @Override
  public Category save(Category categoryToCreate) {
    CategoryEntity categoryToSave = CategoryEntity.from(categoryToCreate);
    CategoryEntity categorySaved = jpaCategoryRepository.save(categoryToSave);
    return CategoryEntity.to(categorySaved);
  }

  @Override
  public Optional<Category> findFeaturedCategory(String product) {
    Optional<CategoryEntity> featuredCategoryFromProduct = jpaCategoryRepository.findFeaturedCategoryFromProduct(product);
    return featuredCategoryFromProduct.map(CategoryEntity::to);
  }
}
