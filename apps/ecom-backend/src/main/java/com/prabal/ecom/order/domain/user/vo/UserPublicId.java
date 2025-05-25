package com.prabal.ecom.order.domain.user.vo;

import com.prabal.ecom.shared.error.domain.Assert;

import java.util.UUID;

public record UserPublicId(UUID value) {

  public UserPublicId {
    Assert.notNull("value", value);
  }
}
