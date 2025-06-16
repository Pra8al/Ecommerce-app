package com.prabal.ecom.order.domain.order.repository;

import com.prabal.ecom.order.domain.order.aggregate.Order;
import com.prabal.ecom.order.domain.order.aggregate.StripeSessionInformation;
import com.prabal.ecom.order.domain.order.vo.OrderStatus;
import com.prabal.ecom.order.domain.user.vo.UserPublicId;
import com.prabal.ecom.product.domain.vo.PublicId;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.Optional;

public interface OrderRepository {

  void save(Order order);

  void updateStatusByPublicId(OrderStatus status, PublicId orderPublicId);

  Optional<Order> findByStripeSessionId(StripeSessionInformation stripeSessionInformation);

//  Page<Order> findAllByUserPublicId(UserPublicId userPublicId, Pageable pageable);
//
//  Page<Order> findAll(Pageable pageable);
}
