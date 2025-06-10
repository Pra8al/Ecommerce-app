package com.prabal.ecom.product.domain.vo;

import com.prabal.ecom.shared.error.domain.Assert;

public record ProductDescription(String value) {
  public ProductDescription {
    Assert.field("value", value).notNull().minLength(10);
  }
}
