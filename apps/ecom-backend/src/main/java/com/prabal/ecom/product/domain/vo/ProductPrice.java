package com.prabal.ecom.product.domain.vo;

import com.prabal.ecom.shared.error.domain.Assert;

public record ProductPrice(double value) {
  public ProductPrice {
    Assert.field("value", value).min(0.1);
  }
}
