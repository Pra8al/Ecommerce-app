package com.prabal.ecom.order.domain.order.aggregate;

import com.prabal.ecom.order.domain.order.vo.OrderStatus;
import com.prabal.ecom.order.domain.order.vo.StripeSessionId;
import com.prabal.ecom.order.domain.user.aggregate.User;
import com.prabal.ecom.product.domain.vo.PublicId;
import org.jilt.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public class Order {

  private OrderStatus orderStatus;

  private final User user;

  private final String stripeId;

  private final PublicId publicId;

  private final List<OrderedProduct> orderedProducts;

  public Order(OrderStatus orderStatus, User user, String stripeId, PublicId publicId, List<OrderedProduct> orderedProducts) {
    this.orderStatus = orderStatus;
    this.user = user;
    this.stripeId = stripeId;
    this.publicId = publicId;
    this.orderedProducts = orderedProducts;
  }

  public static Order create(User connectedUser, List<OrderedProduct> orderedProducts, StripeSessionId stripeSessionId) {
    return OrderBuilder.order()
      .stripeId(stripeSessionId.value())
      .publicId(new PublicId(UUID.randomUUID()))
      .user(connectedUser)
      .orderStatus(OrderStatus.PENDING)
      .orderedProducts(orderedProducts)
      .build();
  }

  public void validatePayment() {
    this.orderStatus = OrderStatus.PAID;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public User getUser() {
    return user;
  }

  public String getStripeId() {
    return stripeId;
  }

  public PublicId getPublicId() {
    return publicId;
  }

  public List<OrderedProduct> getOrderedProducts() {
    return orderedProducts;
  }
}
