package com.prabal.ecom.order.infrastructure.primary.order;

import com.prabal.ecom.order.application.OrderApplicationService;
import com.prabal.ecom.order.domain.order.aggregate.*;
import com.prabal.ecom.order.domain.order.vo.StripeSessionId;
import com.prabal.ecom.order.domain.user.vo.*;
import com.prabal.ecom.order.infrastructure.primary.RestOrderReadAdmin;
import com.prabal.ecom.order.infrastructure.secondary.exceptions.CartPaymentException;
import com.prabal.ecom.product.domain.vo.PublicId;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Address;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.prabal.ecom.product.infrastructure.primary.CategoriesResource.ROLE_ADMIN;

/**
 * REST controller for managing orders in the e-commerce application.
 * Handles cart details, payment initialization, Stripe webhook events,
 * and order retrieval for users and admins.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderResource {

  private final OrderApplicationService orderApplicationService;

  /**
   * Stripe webhook secret, injected from application properties.
   */
  @Value("${application.stripe.webhook-secret}")
  private String webhookSecret;

  /**
   * Constructor for OrderResource.
   *
   * @param orderApplicationService the service handling order operations
   */
  public OrderResource(OrderApplicationService orderApplicationService) {
    this.orderApplicationService = orderApplicationService;
  }

  /**
   * Retrieves detailed cart information for the given product IDs.
   *
   * @param productIds list of product UUIDs
   * @return cart details wrapped in a ResponseEntity
   */
  @GetMapping("/get-cart-details")
  public ResponseEntity<RestDetailCartResponse> getDetails(@RequestParam List<UUID> productIds) {
    List<DetailCartItemRequest> cartItemRequests = productIds.stream()
      .map(uuid -> new DetailCartItemRequest(new PublicId(uuid), 1))
      .toList();

    DetailCartRequest detailCartRequest = DetailCartRequestBuilder.detailCartRequest().items(cartItemRequests).build();
    DetailCartResponse cartDetails = orderApplicationService.getCartDetails(detailCartRequest);
    return ResponseEntity.ok(RestDetailCartResponse.from(cartDetails));
  }

  /**
   * Initializes a payment session for the provided cart items.
   *
   * @param items list of cart item requests
   * @return Stripe session information or bad request if payment fails
   */
  @PostMapping("/init-payment")
  public ResponseEntity<RestStripeSession> initPayment(@RequestBody List<RestCartItemRequest> items) {
    List<DetailCartItemRequest> detailCartItemRequests = RestCartItemRequest.to(items);
    try {
      StripeSessionId stripeSessionInformation = orderApplicationService.createOrder(detailCartItemRequests);
      RestStripeSession restStripeSession = RestStripeSession.from(stripeSessionInformation);
      return ResponseEntity.ok(restStripeSession);
    } catch (CartPaymentException cpe) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Handles Stripe webhook events for order processing.
   *
   * @param paymentEvent    the raw event payload from Stripe
   * @param stripeSignature the Stripe signature header
   * @return HTTP 200 if processed, 400 if signature verification fails
   */
  @PostMapping("/webhook")
  public ResponseEntity<Void> webhookStripe(@RequestBody String paymentEvent,
                                            @RequestHeader("Stripe-Signature") String stripeSignature) {
    Event event = null;
    try {
      event = Webhook.constructEvent(paymentEvent, stripeSignature, webhookSecret);
    } catch (SignatureVerificationException sve) {
      return ResponseEntity.badRequest().build();
    }

    Optional<StripeObject> rawStripeObjectOpt = event.getDataObjectDeserializer().getObject();
    if (event.getType().equals("checkout.session.completed")) {
      handleCheckoutSessionCompleted(rawStripeObjectOpt.orElseThrow());
    }
    return ResponseEntity.ok().build();
  }

  /**
   * Handles the completion of a Stripe checkout session.
   * Updates the order with user address and session information.
   *
   * @param rawStripeObject the Stripe session object
   */
  private void handleCheckoutSessionCompleted(StripeObject rawStripeObject) {
    if (rawStripeObject instanceof Session session) {
      Address address = session.getCustomerDetails().getAddress();
      UserAddress userAddress = UserAddressBuilder.userAddress()
        .country(address.getCountry())
        .city(address.getCity())
        .street(address.getLine1())
        .zipCode(address.getPostalCode())
        .build();

      UserAddressToUpdate userAddressToUpdate = UserAddressToUpdateBuilder.userAddressToUpdate()
        .userAddress(userAddress)
        .userPublicId(new UserPublicId(UUID.fromString(session.getMetadata().get("user_public_id"))))
        .build();

      StripeSessionInformation sessionInformation = StripeSessionInformationBuilder.stripeSessionInformation()
        .userAddress(userAddressToUpdate)
        .stripeSessionId(new StripeSessionId(session.getId()))
        .build();

      orderApplicationService.updateOrder(sessionInformation);
    }
  }

  /**
   * Retrieves paginated orders for the currently connected user.
   *
   * @param pageable pagination information
   * @return paginated list of user orders
   */
  @GetMapping("/user")
  public ResponseEntity<Page<RestOrderRead>> getOrdersForConnectedUser(Pageable pageable) {
    Page<Order> orders = orderApplicationService.findOrdersForConnectedUser(pageable);
    PageImpl<RestOrderRead> restOrderReads = new PageImpl<>(
      orders.getContent().stream().map(RestOrderRead::from).toList(),
      pageable,
      orders.getTotalElements()
    );
    return ResponseEntity.ok(restOrderReads);
  }

  /**
   * Retrieves paginated orders for admin users.
   * Requires admin role.
   *
   * @param pageable pagination information
   * @return paginated list of admin order views
   */
  @GetMapping("/admin")
  @PreAuthorize("hasAnyRole('" + ROLE_ADMIN + "')")
  public ResponseEntity<Page<RestOrderReadAdmin>> getOrdersForAdmin(Pageable pageable) {
    Page<Order> orders = orderApplicationService.findOrdersForAdmin(pageable);
    PageImpl<RestOrderReadAdmin> restOrderReads = new PageImpl<>(
      orders.getContent().stream().map(RestOrderReadAdmin::from).toList(),
      pageable,
      orders.getTotalElements()
    );
    return ResponseEntity.ok(restOrderReads);
  }

}
