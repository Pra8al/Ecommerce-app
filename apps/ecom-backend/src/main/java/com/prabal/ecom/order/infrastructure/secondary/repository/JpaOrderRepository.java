package com.prabal.ecom.order.infrastructure.secondary.repository;

import com.prabal.ecom.order.infrastructure.secondary.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {
}
