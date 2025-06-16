package com.prabal.ecom.order.domain.order.vo;

import com.prabal.ecom.shared.error.domain.Assert;

public record StripeSessionId(String value) {

  public StripeSessionId {
    Assert.notNull("value", value);
  }
}
