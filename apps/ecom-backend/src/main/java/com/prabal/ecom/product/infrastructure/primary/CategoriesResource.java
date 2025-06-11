package com.prabal.ecom.product.infrastructure.primary;

import com.prabal.ecom.product.application.ProductsApplicationService;
import com.prabal.ecom.product.domain.aggregate.Category;
import com.prabal.ecom.product.domain.vo.PublicId;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoriesResource {

  public static final String ROLE_ADMIN = "ROLE_ADMIN";

  private final ProductsApplicationService productsApplicationService;

  private static final Logger log = LoggerFactory.getLogger(CategoriesResource.class);

  public CategoriesResource(ProductsApplicationService productsApplicationService) {
    this.productsApplicationService = productsApplicationService;
  }

  @PreAuthorize("hasAnyRole('" + ROLE_ADMIN + "')")
  @PostMapping
  public ResponseEntity<RestCategory> save(@RequestBody RestCategory restCategory) {
    Category categoryDomain = RestCategory.toDomain(restCategory);
    Category saved = productsApplicationService.createCategory(categoryDomain);
    return ResponseEntity.ok(RestCategory.fromDomain(saved));
  }

  @PreAuthorize("hasAnyRole('" + ROLE_ADMIN + "')")
  @DeleteMapping
  public ResponseEntity<UUID> delete(UUID publicId) {
    try {
      PublicId deletedPublicId = productsApplicationService.deleteCategory(new PublicId(publicId));
      return ResponseEntity.ok(deletedPublicId.value());
    } catch (EntityNotFoundException enfe) {
      log.error("Could not delete category with id {}", publicId, enfe);
      ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, enfe.getMessage());
      return ResponseEntity.of(problemDetail).build();
    }
  }

  @GetMapping
  public ResponseEntity<Page<RestCategory>> findAll(Pageable pageable) {
    Page<Category> allCategory = productsApplicationService.findAllCategory(pageable);
    PageImpl<RestCategory> restCategories = new PageImpl<>(
      allCategory.getContent().stream().map(RestCategory::fromDomain).toList(),
      pageable,
      allCategory.getTotalElements()
    );
    return ResponseEntity.ok(restCategories);
  }
}
