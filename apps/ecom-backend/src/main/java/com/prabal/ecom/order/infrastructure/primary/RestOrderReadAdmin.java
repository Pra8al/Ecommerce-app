package com.prabal.ecom.order.infrastructure.primary;

import com.prabal.ecom.order.domain.order.aggregate.Order;
import com.prabal.ecom.order.domain.order.vo.OrderStatus;
import com.prabal.ecom.order.infrastructure.primary.order.RestOrderedItemRead;
import org.jilt.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record RestOrderReadAdmin(UUID publicId,
                                 OrderStatus status,
                                 List<RestOrderedItemRead> orderedItems,
                                 String address,
                                 String email) {

  public static RestOrderReadAdmin from(Order order) {
    StringBuilder address = new StringBuilder();
    if (order.getUser().getUserAddress() != null) {
      address.append(order.getUser().getUserAddress().street());
      address.append(", ");
      address.append(order.getUser().getUserAddress().city());
      address.append(", ");
      address.append(order.getUser().getUserAddress().zipCode());
      address.append(", ");
      address.append(order.getUser().getUserAddress().country());
    }

    return RestOrderReadAdminBuilder.restOrderReadAdmin()
      .publicId(order.getPublicId().value())
      .status(order.getOrderStatus())
      .orderedItems(RestOrderedItemRead.from(order.getOrderedProducts()))
      .address(address.toString())
      .email(order.getUser().getUserEmail().value())
      .build();
  }
}
