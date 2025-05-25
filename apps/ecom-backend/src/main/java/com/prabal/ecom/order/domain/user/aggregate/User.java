package com.prabal.ecom.order.domain.user.aggregate;

import com.prabal.ecom.order.domain.user.vo.*;
import com.prabal.ecom.shared.error.domain.Assert;
import org.jilt.Builder;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
public class User {
  private UserLastname userLastname;
  private UserFirstname userFirstname;
  private UserEmail userEmail;
  private UserPublicId userPublicId;
  private UserImageUrl userImageUrl;
  private Instant lastModifiedDate;
  private Instant createdDate;
  private Set<Authority> authorities;
  private Long dbId;
  private UserAddress userAddress;
  private Instant lastSeen;

  public User(UserLastname userLastname, UserFirstname userFirstname, UserEmail userEmail, UserPublicId userPublicId, UserImageUrl userImageUrl, Instant lastModifiedDate, Instant createdDate, Set<Authority> authorities, Long dbId, UserAddress userAddress, Instant lastSeen) {
    this.userLastname = userLastname;
    this.userFirstname = userFirstname;
    this.userEmail = userEmail;
    this.userPublicId = userPublicId;
    this.userImageUrl = userImageUrl;
    this.lastModifiedDate = lastModifiedDate;
    this.createdDate = createdDate;
    this.authorities = authorities;
    this.dbId = dbId;
    this.userAddress = userAddress;
    this.lastSeen = lastSeen;
  }

  private void assertMandatoryFields() {
    Assert.notNull("userLastname", userLastname);
    Assert.notNull("userFirstname", userFirstname);
    Assert.notNull("userEmail", userEmail);
    Assert.notNull("authorities", authorities);
  }

  public void updateFromUser(User user) {
    this.userLastname = user.userLastname;
    this.userFirstname = user.userFirstname;
    this.userEmail = user.userEmail;
    this.userImageUrl = user.userImageUrl;
  }

  public void initFieldForSignup() {
    this.userPublicId = new UserPublicId(UUID.randomUUID());
  }

  public static User fromTokenAttributes(Map<String, Object> attributes, List<String> rolesFromAccessToken) {
    UserBuilder userBuilder = UserBuilder.user();

    if (attributes.containsKey("preferred_email")) {
      userBuilder.userEmail(new UserEmail((String) attributes.get("preferred_email")));
    }

    if (attributes.containsKey("family_name")) {
      userBuilder.userLastname(new UserLastname((String) attributes.get("family_name")));
    }
    if (attributes.containsKey("given_name")) {
      userBuilder.userFirstname(new UserFirstname((String) attributes.get("given_name")));
    }

    if (attributes.containsKey("picture")) {
      userBuilder.userImageUrl(new UserImageUrl((String) attributes.get("picture")));
    }

    if (attributes.containsKey("last_signed_in")) {
      userBuilder.lastSeen(Instant.parse(attributes.get("last_signed_in").toString()));
    }

    Set<Authority> authorities = rolesFromAccessToken
      .stream()
      .map(authority -> AuthorityBuilder.authority().name(new AuthorityName(authority)).build())
      .collect(Collectors.toSet());

    userBuilder.authorities(authorities);
    userBuilder.createdDate(Instant.now());
    userBuilder.lastModifiedDate(Instant.now());
    return userBuilder.build();


  }

  public UserLastname getUserLastname() {
    return userLastname;
  }

  public UserFirstname getUserFirstname() {
    return userFirstname;
  }

  public UserEmail getUserEmail() {
    return userEmail;
  }

  public UserPublicId getUserPublicId() {
    return userPublicId;
  }

  public UserImageUrl getUserImageUrl() {
    return userImageUrl;
  }

  public Instant getLastModifiedDate() {
    return lastModifiedDate;
  }

  public Instant getCreatedDate() {
    return createdDate;
  }

  public Set<Authority> getAuthorities() {
    return authorities;
  }

  public Long getDbId() {
    return dbId;
  }

  public UserAddress getUserAddress() {
    return userAddress;
  }

  public Instant getLastSeen() {
    return lastSeen;
  }
}
