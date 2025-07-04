package com.prabal.ecom.order.domain.order.service;

import com.prabal.ecom.order.domain.order.aggregate.*;
import com.prabal.ecom.order.domain.order.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

public class OrderUpdater {

  private final OrderRepository orderRepository;

  public OrderUpdater(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public List<OrderedProduct> updateOrderFromStripe(StripeSessionInformation stripeSessionInformation) {
    Order order = orderRepository.findByStripeSessionId(stripeSessionInformation).orElseThrow();
    order.validatePayment();
    orderRepository.updateStatusByPublicId(order.getOrderStatus(), order.getPublicId());
    return order.getOrderedProducts();
  }

  public List<OrderProductQuantity> computeQuantity(List<OrderedProduct> orderedProducts) {
    List<OrderProductQuantity> orderProductQuantities = new ArrayList<>();
    for (OrderedProduct orderedProduct : orderedProducts) {
      OrderProductQuantity orderProductQuantity = OrderProductQuantityBuilder.orderProductQuantity()
        .productPublicId(orderedProduct.getProductPublicId())
        .quantity(orderedProduct.getQuantity())
        .build();
      orderProductQuantities.add(orderProductQuantity);
    }
    return orderProductQuantities;
  }
}
