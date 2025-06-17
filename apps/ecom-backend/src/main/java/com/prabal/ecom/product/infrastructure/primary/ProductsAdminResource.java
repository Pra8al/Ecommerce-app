package com.prabal.ecom.product.infrastructure.primary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prabal.ecom.product.application.ProductsApplicationService;
import com.prabal.ecom.product.domain.aggregate.Product;
import com.prabal.ecom.product.domain.vo.PublicId;
import com.prabal.ecom.product.infrastructure.primary.exceptions.MultipartPictureException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.prabal.ecom.product.infrastructure.primary.CategoriesResource.ROLE_ADMIN;

/**
 * REST controller for managing products in the admin context.
 * Provides endpoints for creating, deleting, and retrieving products,
 * including handling multipart file uploads for product images.
 */
@RestController
@RequestMapping("/api/products")
public class ProductsAdminResource {

  /**
   * Logger for this class.
   */
  private static final Logger log = LoggerFactory.getLogger(ProductsAdminResource.class);

  /**
   * Service for product-related operations.
   */
  private final ProductsApplicationService productsApplicationService;

  /**
   * ObjectMapper for JSON deserialization.
   */
  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Constructs a new ProductsAdminResource with the given ProductsApplicationService.
   *
   * @param productsApplicationService the service handling product operations
   */
  public ProductsAdminResource(ProductsApplicationService productsApplicationService) {
    this.productsApplicationService = productsApplicationService;
  }

  /**
   * Creates a new product with associated images.
   * Only accessible by users with the admin role.
   * Expects a multipart request with product data and image files.
   *
   * @param request    the multipart HTTP request containing image files
   * @param productRaw the product data as a JSON string
   * @return the created product wrapped in a ResponseEntity
   * @throws JsonProcessingException if the product JSON cannot be parsed
   */
  @PreAuthorize("hasAnyRole('" + ROLE_ADMIN + "')")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<RestProduct> save(MultipartHttpServletRequest request,
                                          @RequestParam("dto") String productRaw) throws JsonProcessingException {
    List<RestPicture> restPictures = request.getFileMap()
      .values()
      .stream()
      .map(mapMultipartFileToRestPicture())
      .toList();

    RestProduct restProduct = objectMapper.readValue(productRaw, RestProduct.class);
    restProduct.addPictureAttachment(restPictures);

    Product newProduct = RestProduct.toDomain(restProduct);
    Product product = productsApplicationService.createProduct(newProduct);
    return ResponseEntity.ok(RestProduct.fromDomain(product));
  }

  /**
   * Maps a MultipartFile to a RestPicture, handling IO exceptions.
   *
   * @return a function that converts MultipartFile to RestPicture
   * @throws MultipartPictureException if the file cannot be read
   */
  private Function<MultipartFile, RestPicture> mapMultipartFileToRestPicture() {
    return multipartFile -> {
      try {
        return new RestPicture(multipartFile.getBytes(), multipartFile.getContentType());
      } catch (IOException ioe) {
        throw new MultipartPictureException(String.format("Cannot parse multipart file : %s", multipartFile.getOriginalFilename()));
      }
    };
  }

  /**
   * Deletes a product by its public ID.
   * Only accessible by users with the admin role.
   *
   * @param id the UUID of the product to delete
   * @return the UUID of the deleted product, or a ProblemDetail if not found
   */
  @PreAuthorize("hasAnyRole('" + ROLE_ADMIN + "')")
  @DeleteMapping
  public ResponseEntity<UUID> delete(@RequestParam("publicId") UUID id) {
    try {
      PublicId deletedProductUUID = productsApplicationService.deleteProduct(new PublicId(id));
      return ResponseEntity.ok(deletedProductUUID.value());
    } catch (EntityNotFoundException enfe) {
      log.error("Could not delete category with id {}", id, enfe);
      ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, enfe.getMessage());
      return ResponseEntity.of(problemDetail).build();
    }
  }

  /**
   * Retrieves all products with pagination support.
   *
   * @param pageable pagination information
   * @return a paginated list of products wrapped in a ResponseEntity
   */
  @GetMapping
  public ResponseEntity<Page<RestProduct>> getAll(Pageable pageable) {
    Page<Product> allProducts = productsApplicationService.findAllProduct(pageable);
    PageImpl<RestProduct> restProducts = new PageImpl<>(
      allProducts.getContent().stream().map(RestProduct::fromDomain).toList(),
      pageable,
      allProducts.getTotalElements()
    );
    return ResponseEntity.ok(restProducts);
  }
}
