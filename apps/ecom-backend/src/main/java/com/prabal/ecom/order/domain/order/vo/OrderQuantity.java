package com.prabal.ecom.order.domain.order.vo;

import com.prabal.ecom.shared.error.domain.Assert;

public record OrderQuantity(long value) {

  public OrderQuantity {
    Assert.field("value", value).positive();
  }
}
