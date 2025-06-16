package com.prabal.ecom.order.domain.order.repository;

import com.prabal.ecom.order.domain.order.aggregate.Order;

public interface OrderRepository {

  void save(Order order);
}
