package com.prabal.ecom.product.domain.service;

import com.prabal.ecom.product.domain.aggregate.Product;
import com.prabal.ecom.product.domain.repository.ProductRepository;
import com.prabal.ecom.product.domain.vo.PublicId;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class ProductCRUD {
  private final ProductRepository productRepository;

  public ProductCRUD(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public Product save(Product newProduct) {
    newProduct.initDefaultFields();
    return productRepository.save(newProduct);
  }

  public Page<Product> findAll(Pageable pageable) {
    return productRepository.findAll(pageable);
  }

  public PublicId remove(PublicId id) {
    int deleted = productRepository.delete(id);
    if (deleted != 1) {
      throw new EntityNotFoundException(String.format("No product deleted with id: %s", id));
    }
    return id;
  }
}
