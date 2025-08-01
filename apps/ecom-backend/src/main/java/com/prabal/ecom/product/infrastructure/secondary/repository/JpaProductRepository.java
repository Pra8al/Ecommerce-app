package com.prabal.ecom.product.infrastructure.secondary.repository;

import com.prabal.ecom.product.domain.vo.ProductSize;
import com.prabal.ecom.product.infrastructure.secondary.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaProductRepository  extends JpaRepository<ProductEntity, Long> {

  Optional<ProductEntity> findByPublicId(UUID publicId);

  int deleteByPublicId(UUID publicId);

  Page<ProductEntity> findAllByFeaturedTrue(Pageable pageable);

  Page<ProductEntity> findByCategoryPublicIdAndPublicIdNot(Pageable pageable, UUID categoryPublicId, UUID excludedProductPublicId);

  @Query("SELECT product FROM ProductEntity product WHERE (:sizes is null or product.size IN (:sizes)) AND " +
    "product.category.publicId = :categoryPublicId")
  Page<ProductEntity> findByCategoryPublicIdAndSizesIn(Pageable pageable, UUID categoryPublicId, List<ProductSize> sizes);

  List<ProductEntity> findAllByPublicIdIn(List<UUID> publicIds);

  @Modifying
  @Query("UPDATE ProductEntity  product " +
    "SET product.numberInStock = product.numberInStock - :quantity " +
    "WHERE product.publicId = :productPublicId")
  void updateQuantity(UUID productPublicId, long quantity);

}
