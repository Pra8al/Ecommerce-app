package com.prabal.ecom.order.infrastructure.secondary.entity;

import com.prabal.ecom.order.domain.order.aggregate.OrderedProduct;
import com.prabal.ecom.order.domain.order.aggregate.OrderedProductBuilder;
import com.prabal.ecom.order.domain.order.vo.OrderPrice;
import com.prabal.ecom.order.domain.order.vo.OrderQuantity;
import com.prabal.ecom.order.domain.order.vo.ProductPublicId;
import com.prabal.ecom.product.domain.vo.ProductName;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.jilt.Builder;

import java.util.List;

@Entity
@Table(name = "ordered_product")
@Builder
public class OrderedProductEntity {

  @EmbeddedId
  private OrderedProductEntityPk id;

  @Column(name = "price", nullable = false)
  private Double price;

  @Column(name = "quantity", nullable = false)
  private long quantity;

  @Column(name = "product_name", nullable = false)
  private String productName;

  public OrderedProductEntity() {
  }

  public OrderedProductEntity(OrderedProductEntityPk id, Double price, long quantity, String productName) {
    this.id = id;
    this.price = price;
    this.quantity = quantity;
    this.productName = productName;
  }

  public static OrderedProductEntity from(OrderedProduct orderedProduct) {
    OrderedProductEntityPk orderedProductEntityPk = OrderedProductEntityPkBuilder.orderedProductEntityPk()
      .productPublicId(orderedProduct.getProductPublicId().value())
      .build();

    return OrderedProductEntityBuilder.orderedProductEntity()
      .id(orderedProductEntityPk)
      .productName(orderedProduct.getProductName().value())
      .price(orderedProduct.getPrice().value())
      .quantity(orderedProduct.getQuantity().value())
      .build();
  }

  public static OrderedProduct to(OrderedProductEntity orderedProductEntity) {
    return OrderedProductBuilder.orderedProduct()
      .productPublicId(new ProductPublicId(orderedProductEntity.getId().getProductPublicId()))
      .quantity((new OrderQuantity(orderedProductEntity.getQuantity())))
      .price(new OrderPrice(orderedProductEntity.getPrice()))
      .productName(new ProductName(orderedProductEntity.getProductName()))
      .build();
  }

  public static List<OrderedProductEntity> from(List<OrderedProduct> orderedProducts) {
    return orderedProducts.stream().map(OrderedProductEntity::from).toList();
  }

  public OrderedProductEntityPk getId() {
    return id;
  }

  public void setId(OrderedProductEntityPk id) {
    this.id = id;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public long getQuantity() {
    return quantity;
  }

  public void setQuantity(long quantity) {
    this.quantity = quantity;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }
}
