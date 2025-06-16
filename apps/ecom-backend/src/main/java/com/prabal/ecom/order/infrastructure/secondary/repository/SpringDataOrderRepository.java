package com.prabal.ecom.order.infrastructure.secondary.repository;

import com.prabal.ecom.order.domain.order.aggregate.Order;
import com.prabal.ecom.order.domain.order.repository.OrderRepository;
import com.prabal.ecom.order.infrastructure.secondary.entity.OrderEntity;
import org.springframework.stereotype.Repository;

@Repository
public class SpringDataOrderRepository implements OrderRepository {

  private final JpaOrderRepository jpaOrderRepository;
  private final JpaOrderedProductRepository jpaOrderedProductRepository;

  public SpringDataOrderRepository(JpaOrderRepository jpaOrderRepository, JpaOrderedProductRepository jpaOrderedProductRepository) {
    this.jpaOrderRepository = jpaOrderRepository;
    this.jpaOrderedProductRepository = jpaOrderedProductRepository;
  }

  @Override
  public void save(Order order) {
    OrderEntity orderEntityToSave = OrderEntity.from(order);
    OrderEntity savedOrderEntity = jpaOrderRepository.save(orderEntityToSave);

    savedOrderEntity.getOrderedProducts()
      .forEach(orderedProductEntity -> orderedProductEntity.getId().setOrder(savedOrderEntity));

    jpaOrderedProductRepository.saveAll(savedOrderEntity.getOrderedProducts());
  }
}
