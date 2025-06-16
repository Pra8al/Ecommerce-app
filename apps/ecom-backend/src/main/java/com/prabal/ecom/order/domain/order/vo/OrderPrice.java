package com.prabal.ecom.order.domain.order.vo;

import com.prabal.ecom.shared.error.domain.Assert;

public record OrderPrice(double value) {

  public OrderPrice {
    Assert.field("value", value).strictlyPositive();
  }

}
