package com.prabal.ecom.order.application;

import com.prabal.ecom.order.domain.order.aggregate.DetailCartItemRequest;
import com.prabal.ecom.order.domain.order.aggregate.DetailCartRequest;
import com.prabal.ecom.order.domain.order.aggregate.DetailCartResponse;
import com.prabal.ecom.order.domain.order.repository.OrderRepository;
import com.prabal.ecom.order.domain.order.service.CartReader;
import com.prabal.ecom.order.domain.order.service.OrderCreator;
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

  public OrderApplicationService(ProductsApplicationService productsApplicationService,
                                 UsersApplicationService usersApplicationService,
                                 OrderRepository orderRepository,
                                 StripeService stripeService) {
    this.productsApplicationService = productsApplicationService;
    this.usersApplicationService = usersApplicationService;
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


}
