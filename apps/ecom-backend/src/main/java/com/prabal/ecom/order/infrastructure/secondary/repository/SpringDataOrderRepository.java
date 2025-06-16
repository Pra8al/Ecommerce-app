package com.prabal.ecom.order.infrastructure.secondary.repository;

import com.prabal.ecom.order.domain.order.aggregate.Order;
import com.prabal.ecom.order.domain.order.aggregate.StripeSessionInformation;
import com.prabal.ecom.order.domain.order.repository.OrderRepository;
import com.prabal.ecom.order.domain.order.vo.OrderStatus;
import com.prabal.ecom.order.domain.user.vo.UserPublicId;
import com.prabal.ecom.order.infrastructure.secondary.entity.OrderEntity;
import com.prabal.ecom.product.domain.vo.PublicId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

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

  @Override
  public void updateStatusByPublicId(OrderStatus status, PublicId orderPublicId) {
    jpaOrderRepository.updateStatusByPublicId(status, orderPublicId.value());
  }

  @Override
  public Optional<Order> findByStripeSessionId(StripeSessionInformation stripeSessionInformation) {
    return jpaOrderRepository.findByStripeSessionId(stripeSessionInformation.stripeSessionId().value())
      .map(OrderEntity::to);
  }

  @Override
  public Page<Order> findAllByUserPublicId(UserPublicId userPublicId, Pageable pageable) {
    return jpaOrderRepository.findAllByUserPublicId(userPublicId.value(), pageable)
      .map(OrderEntity::to);
  }

  @Override
  public Page<Order> findAll(Pageable pageable) {
    return jpaOrderRepository.findAll(pageable).map(OrderEntity::to);
  }
}
