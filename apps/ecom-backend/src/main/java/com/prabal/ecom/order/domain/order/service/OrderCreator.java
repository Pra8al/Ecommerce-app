package com.prabal.ecom.order.domain.order.service;

import com.prabal.ecom.order.domain.order.aggregate.DetailCartItemRequest;
import com.prabal.ecom.order.domain.order.aggregate.Order;
import com.prabal.ecom.order.domain.order.aggregate.OrderedProduct;
import com.prabal.ecom.order.domain.order.repository.OrderRepository;
import com.prabal.ecom.order.domain.order.vo.StripeSessionId;
import com.prabal.ecom.order.domain.user.aggregate.User;
import com.prabal.ecom.order.infrastructure.secondary.service.stripe.StripeService;
import com.prabal.ecom.product.domain.aggregate.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderCreator {

  private final OrderRepository orderRepository;
  private final StripeService stripeService;

  public OrderCreator(OrderRepository orderRepository, StripeService stripeService) {
    this.orderRepository = orderRepository;
    this.stripeService = stripeService;
  }

  public StripeSessionId create(List<Product> productsInformation,
                                List<DetailCartItemRequest> items,
                                User connectedUser){
    List<OrderedProduct> orderedProducts = new ArrayList<>();
    StripeSessionId stripeSessionId = this.stripeService.createPayment(connectedUser, productsInformation, items);

    for(DetailCartItemRequest itemRequest: items){
      Product productDetails = productsInformation.stream()
        .filter(product -> product.getPublicId().value().equals(itemRequest.productId().value()))
        .findFirst().orElseThrow();

      OrderedProduct orderedProduct = OrderedProduct.create(itemRequest.quantity(), productDetails);
      orderedProducts.add(orderedProduct);
    }

    Order order = Order.create(connectedUser, orderedProducts, stripeSessionId);
    orderRepository.save(order);

    return stripeSessionId;
  }

}
