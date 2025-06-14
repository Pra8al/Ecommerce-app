package com.prabal.ecom.product.infrastructure.primary;

import com.prabal.ecom.product.application.ProductsApplicationService;
import com.prabal.ecom.product.domain.aggregate.FilterQueryBuilder;
import com.prabal.ecom.product.domain.aggregate.Product;
import com.prabal.ecom.product.domain.vo.ProductSize;
import com.prabal.ecom.product.domain.vo.PublicId;
import jakarta.persistence.EntityNotFoundException;
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

@RestController
@RequestMapping("/api/products-shop")
public class ProductShopResource {

  private final ProductsApplicationService productsApplicationService;

  public ProductShopResource(ProductsApplicationService productsApplicationService) {
    this.productsApplicationService = productsApplicationService;
  }

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

  @GetMapping("/find-one")
  public ResponseEntity<RestProduct> getOne(@RequestParam("publicId") UUID id) {
    Optional<Product> productOptional = productsApplicationService.findOne(new PublicId(id));
    return productOptional.map(product -> ResponseEntity.ok(RestProduct.fromDomain(product)))
      .orElseGet(() -> ResponseEntity.badRequest().build());
  }

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

  @GetMapping("/filter")
  public ResponseEntity<Page<RestProduct>> filter(Pageable pageable,
                                                  @RequestParam("categoryId") UUID categoryId,
                                                  @RequestParam(value = "productSizes", required = false) List<ProductSize> sizes) {
    FilterQueryBuilder filterQueryBuilder = FilterQueryBuilder.filterQuery().categoryId(new PublicId(categoryId));
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
