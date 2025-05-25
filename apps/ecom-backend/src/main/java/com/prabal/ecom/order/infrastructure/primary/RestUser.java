package com.prabal.ecom.order.infrastructure.primary;

import com.prabal.ecom.order.domain.user.aggregate.User;
import org.jilt.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record RestUser(UUID publicId,
                       String firstName,
                       String lastName,
                       String email,
                       String imageUrl,
                       Set<String> authorities) {

  public static RestUser from(User user){
    RestUserBuilder restUserBuilder = RestUserBuilder.restUser();
    if(user.getUserImageUrl() != null) {
      restUserBuilder.imageUrl(user.getUserImageUrl().value());
    }

    return restUserBuilder
      .email(user.getUserEmail().value())
      .firstName(user.getUserEmail().value())
      .lastName(user.getUserLastname().value())
      .publicId(user.getUserPublicId().value())
      .authorities(RestAuthority.fromSet(user.getAuthorities()))
      .build();
  }

}
