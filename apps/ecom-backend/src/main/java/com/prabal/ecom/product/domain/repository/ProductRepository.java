package com.prabal.ecom.product.domain.repository;

import com.prabal.ecom.product.domain.aggregate.Product;
import com.prabal.ecom.product.domain.vo.PublicId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;


public interface ProductRepository {

  Product save(Product productToCreate);

  Page<Product> findAll(Pageable pageable);

  int delete(PublicId publicId);

  Page<Product> findAllFeaturedProduct(Pageable pageable);

  Optional<Product> findOne(PublicId publicId);

  Page<Product> findByCategoryExcludingOne(Pageable pageable, PublicId categoryPublicId, PublicId productPublicId);
}
