package com.prabal.ecom.order.infrastructure.primary;

import com.prabal.ecom.order.application.UsersApplicationService;
import com.prabal.ecom.order.domain.user.aggregate.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user-related endpoints.
 * Provides operations to retrieve authenticated user information.
 */
@RestController
@RequestMapping("/api/users")
public class UserResource {

  private final UsersApplicationService usersApplicationService;

  /**
   * Constructs a new UserResource with the given UsersApplicationService.
   *
   * @param usersApplicationService the service handling user operations
   */
  public UserResource(UsersApplicationService usersApplicationService) {
    this.usersApplicationService = usersApplicationService;
  }

  /**
   * Retrieves the currently authenticated user.
   * Optionally forces a resynchronization with the user data source.
   *
   * @param jwtToken    the JWT token of the authenticated user, injected by Spring Security
   * @param forceResync if true, forces a resync of user data
   * @return the authenticated user wrapped in a ResponseEntity
   */
  @GetMapping("/authenticated")
  public ResponseEntity<RestUser> getAuthenticatedUser(@AuthenticationPrincipal Jwt jwtToken,
                                                       @RequestParam boolean forceResync) {
    User authenticatedUser = usersApplicationService.getAuthenticatedUserWithSync(jwtToken, forceResync);
    RestUser restUser = RestUser.from(authenticatedUser);
    return ResponseEntity.ok(restUser);

  }
}
