package com.prabal.ecom.product.domain.service;

import com.prabal.ecom.product.domain.aggregate.Product;
import com.prabal.ecom.product.domain.repository.ProductRepository;
import com.prabal.ecom.product.domain.vo.PublicId;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class ProductShop {

  private final ProductRepository productRepository;

  public ProductShop(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public Page<Product> getFeaturedProducts(Pageable pageable) {
    return productRepository.findAllFeaturedProduct(pageable);
  }

  public Page<Product> findRelated(Pageable pageable, PublicId productPublicId) {
    Optional<Product> productOptional = productRepository.findOne(productPublicId);
    if (productOptional.isPresent()) {
      return productRepository.findByCategoryExcludingOne(pageable,
        productOptional.get().getCategory().getPublicId(),
        productPublicId);
    } else {
      throw new EntityNotFoundException(String.format("No product found with publicId %s", productPublicId));
    }
  }
}
