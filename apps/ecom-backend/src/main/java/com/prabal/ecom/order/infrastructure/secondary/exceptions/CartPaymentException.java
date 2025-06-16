package com.prabal.ecom.order.infrastructure.secondary.exceptions;

public class CartPaymentException extends RuntimeException {
  public CartPaymentException(String message) {
    super(message);
  }
}
