package com.prabal.ecom.order.infrastructure.secondary.repository;

import com.prabal.ecom.order.domain.order.vo.OrderStatus;
import com.prabal.ecom.order.infrastructure.secondary.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {

  @Modifying
  @Query("UPDATE OrderEntity order SET order.status = :orderStatus WHERE order.publicId = :orderPublicId")
  void updateStatusByPublicId(OrderStatus orderStatus, UUID orderPublicId);

  Optional<OrderEntity> findByStripeSessionId(String stripeSessionId);
}
