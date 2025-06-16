package com.prabal.ecom.order.infrastructure.primary.order;

import com.prabal.ecom.product.domain.aggregate.ProductCart;
import com.prabal.ecom.product.infrastructure.primary.RestPicture;
import org.jilt.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record RestProductCart(String name,
                              double price,
                              String brand,
                              RestPicture picture,
                              int quantity,
                              UUID publicId) {

  public static RestProductCart from(ProductCart productCart) {
    return RestProductCartBuilder.restProductCart()
      .name(productCart.getName().value())
      .brand(productCart.getBrand().value())
      .price(productCart.getPrice().value())
      .picture(RestPicture.fromDomain(productCart.getPicture()))
      .publicId(productCart.getPublicId().value())
      .build();
  }

  public static List<RestProductCart> from(List<ProductCart> productCarts){
    return productCarts.stream().map(RestProductCart::from).toList();

  }
}
