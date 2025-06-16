package com.prabal.ecom.product.domain.service;

import com.prabal.ecom.order.domain.order.aggregate.OrderProductQuantity;
import com.prabal.ecom.order.infrastructure.secondary.entity.OrderedProductEntity;
import com.prabal.ecom.product.domain.repository.ProductRepository;

import java.util.List;

public class ProductUpdater {

  private final ProductRepository productRepository;

  public ProductUpdater(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public void updateProductQuantity(List<OrderProductQuantity> orderProductQuantities) {
    for (OrderProductQuantity orderProductQuantity : orderProductQuantities) {
      productRepository.updateQuantity(orderProductQuantity.productPublicId(), orderProductQuantity.quantity().value());
    }
  }
}
