package com.prabal.ecom.order.application;

import com.prabal.ecom.order.domain.order.aggregate.*;
import com.prabal.ecom.order.domain.order.repository.OrderRepository;
import com.prabal.ecom.order.domain.order.service.CartReader;
import com.prabal.ecom.order.domain.order.service.OrderCreator;
import com.prabal.ecom.order.domain.order.service.OrderUpdater;
import com.prabal.ecom.order.domain.order.vo.StripeSessionId;
import com.prabal.ecom.order.domain.user.aggregate.User;
import com.prabal.ecom.order.infrastructure.secondary.service.stripe.StripeService;
import com.prabal.ecom.product.application.ProductsApplicationService;
import com.prabal.ecom.product.domain.aggregate.Product;
import com.prabal.ecom.product.domain.vo.PublicId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderApplicationService {

  private final ProductsApplicationService productsApplicationService;

  private final CartReader cartReader;

  private final UsersApplicationService usersApplicationService;

  private final OrderCreator orderCreator;

  private final OrderUpdater orderUpdater;

  public OrderApplicationService(ProductsApplicationService productsApplicationService,
                                 UsersApplicationService usersApplicationService,
                                 OrderRepository orderRepository,
                                 StripeService stripeService) {
    this.productsApplicationService = productsApplicationService;
    this.usersApplicationService = usersApplicationService;
    this.orderUpdater = new OrderUpdater(orderRepository);
    this.orderCreator = new OrderCreator(orderRepository, stripeService);
    this.cartReader = new CartReader();
  }

  @Transactional(readOnly = true)
  public DetailCartResponse getCartDetails(DetailCartRequest detailCartRequest) {
    List<PublicId> publicIds = detailCartRequest.items().stream().map(DetailCartItemRequest::productId).toList();
    List<Product> productsInFormation = productsApplicationService.getProductByPublicIdsIn(publicIds);
    return cartReader.getDetails(productsInFormation);
  }

  @Transactional
  public StripeSessionId createOrder(List<DetailCartItemRequest> items) {
    User authenticatedUser = usersApplicationService.getAuthenticatedUser();
    List<PublicId> publicIds = items.stream().map(DetailCartItemRequest::productId).toList();
    List<Product> productsInformation = productsApplicationService.getProductByPublicIdsIn(publicIds);
    return orderCreator.create(productsInformation, items, authenticatedUser);
  }

  @Transactional
  public void updateOrder(StripeSessionInformation stripeSessionInformation) {
    List<OrderedProduct> orderedProducts = this.orderUpdater.updateOrderFromStripe(stripeSessionInformation);
    List<OrderProductQuantity> orderProductQuantities = this.orderUpdater.computeQuantity(orderedProducts);
    this.productsApplicationService.updateProductQuantity(orderProductQuantities);
    this.usersApplicationService.updateAddress(stripeSessionInformation.userAddress());
  }

}
