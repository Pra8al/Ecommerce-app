package com.prabal.ecom.product.infrastructure.primary;

import com.prabal.ecom.product.application.ProductsApplicationService;
import com.prabal.ecom.product.domain.aggregate.Category;
import com.prabal.ecom.product.domain.aggregate.FilterQueryBuilder;
import com.prabal.ecom.product.domain.aggregate.Product;
import com.prabal.ecom.product.domain.vo.ProductSize;
import com.prabal.ecom.product.domain.vo.PublicId;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for handling product shop related endpoints.
 * Provides APIs for fetching featured products, finding a product by public ID,
 * retrieving related products, and filtering products by category and size.
 */
@RestController
@RequestMapping("/api/products-shop")
public class ProductShopResource {

  private final ProductsApplicationService productsApplicationService;

  /**
   * Constructor for dependency injection of ProductsApplicationService.
   *
   * @param productsApplicationService the application service for product operations
   */
  public ProductShopResource(ProductsApplicationService productsApplicationService) {
    this.productsApplicationService = productsApplicationService;
  }

  /**
   * The name of the featured category, injected from application properties.
   */
  @Value("${product.featured-category}")
  String featuredCategory;

  /**
   * Retrieves a paginated list of featured products.
   *
   * @param pageable the pagination information
   * @return a ResponseEntity containing a page of RestProduct objects
   */
  @GetMapping("/featured")
  public ResponseEntity<Page<RestProduct>> getAllFeatured(Pageable pageable) {
    Page<Product> products = productsApplicationService.getFeaturedProduct(pageable);
    PageImpl<RestProduct> restProducts = new PageImpl<>(
      products.getContent().stream().map(RestProduct::fromDomain).toList(),
      pageable,
      products.getTotalElements()
    );
    return ResponseEntity.ok(restProducts);
  }

  /**
   * Retrieves a single product by its public ID.
   *
   * @param id the UUID of the product's public ID
   * @return a ResponseEntity containing the RestProduct if found, or bad request if not found
   */
  @GetMapping("/find-one")
  public ResponseEntity<RestProduct> getOne(@RequestParam("publicId") UUID id) {
    Optional<Product> productOptional = productsApplicationService.findOne(new PublicId(id));
    return productOptional.map(product -> ResponseEntity.ok(RestProduct.fromDomain(product)))
      .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  /**
   * Retrieves a paginated list of products related to the given product public ID.
   *
   * @param pageable the pagination information
   * @param id       the UUID of the product's public ID
   * @return a ResponseEntity containing a page of related RestProduct objects, or bad request if not found
   */
  @GetMapping("/related")
  public ResponseEntity<Page<RestProduct>> findRelated(Pageable pageable,
                                                       @RequestParam("publicId") UUID id) {
    try {
      Page<Product> products = productsApplicationService.findRelatedProducts(pageable, new PublicId(id));
      PageImpl<RestProduct> restProducts = new PageImpl<>(
        products.getContent().stream().map(RestProduct::fromDomain).toList(),
        pageable,
        products.getTotalElements()
      );
      return ResponseEntity.ok(restProducts);
    } catch (EntityNotFoundException enfe) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Filters products by category and/or product sizes.
   * If categoryId is not provided, uses the featured category.
   *
   * @param pageable   the pagination information
   * @param categoryId the UUID of the category to filter by (optional)
   * @param sizes      the list of product sizes to filter by (optional)
   * @return a ResponseEntity containing a page of filtered RestProduct objects
   */
  @GetMapping("/filter")
  public ResponseEntity<Page<RestProduct>> filter(Pageable pageable,
                                                  @RequestParam(value = "categoryId", required = false) UUID categoryId,
                                                  @RequestParam(value = "productSizes", required = false) List<ProductSize> sizes) {
    FilterQueryBuilder filterQueryBuilder = FilterQueryBuilder.filterQuery();

    if (categoryId == null) {
      Category category = this.productsApplicationService.getFeaturedProductCategory(featuredCategory);
      filterQueryBuilder.categoryId(new PublicId(category.getPublicId().value()));
    } else {
      filterQueryBuilder.categoryId(new PublicId(categoryId));
    }
    if (sizes != null) {
      filterQueryBuilder.sizes(sizes);
    }

    Page<Product> filter = productsApplicationService.filter(pageable, filterQueryBuilder.build());
    PageImpl<RestProduct> restProducts = new PageImpl<>(
      filter.getContent().stream().map(RestProduct::fromDomain).toList(),
      pageable,
      filter.getTotalElements()
    );
    return ResponseEntity.ok(restProducts);
  }
}
