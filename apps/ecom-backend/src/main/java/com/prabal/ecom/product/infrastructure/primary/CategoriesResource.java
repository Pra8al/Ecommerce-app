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

/**
 * REST controller for managing product categories.
 * Provides endpoints for creating, deleting, and retrieving categories.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoriesResource {

  /**
   * Role constant for admin authorization.
   */
  public static final String ROLE_ADMIN = "ROLE_ADMIN";

  private final ProductsApplicationService productsApplicationService;

  private static final Logger log = LoggerFactory.getLogger(CategoriesResource.class);

  /**
   * Constructs a new CategoriesResource with the given ProductsApplicationService.
   *
   * @param productsApplicationService the service handling category operations
   */
  public CategoriesResource(ProductsApplicationService productsApplicationService) {
    this.productsApplicationService = productsApplicationService;
  }

  /**
   * Creates a new category.
   * Only accessible by users with the admin role.
   *
   * @param restCategory the category data from the request body
   * @return the created category wrapped in a ResponseEntity
   */
  @PreAuthorize("hasAnyRole('" + ROLE_ADMIN + "')")
  @PostMapping
  public ResponseEntity<RestCategory> save(@RequestBody RestCategory restCategory) {
    Category categoryDomain = RestCategory.toDomain(restCategory);
    Category saved = productsApplicationService.createCategory(categoryDomain);
    return ResponseEntity.ok(RestCategory.fromDomain(saved));
  }

  /**
   * Deletes a category by its public ID.
   * Only accessible by users with the admin role.
   *
   * @param publicId the UUID of the category to delete
   * @return the UUID of the deleted category, or a ProblemDetail if not found
   */
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

  /**
   * Retrieves all categories with pagination support.
   *
   * @param pageable pagination information
   * @return a paginated list of categories wrapped in a ResponseEntity
   */
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
