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

@RestController
@RequestMapping("/api/products")
public class ProductsAdminResource {

  private static final Logger log = LoggerFactory.getLogger(ProductsAdminResource.class);

  private final ProductsApplicationService productsApplicationService;

  public static final String ROLE_ADMIN = "ROLE_ADMIN";

  private final ObjectMapper objectMapper = new ObjectMapper();

  public ProductsAdminResource(ProductsApplicationService productsApplicationService) {
    this.productsApplicationService = productsApplicationService;
  }


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

  private Function<MultipartFile, RestPicture> mapMultipartFileToRestPicture() {
    return multipartFile -> {
      try {
        return new RestPicture(multipartFile.getBytes(), multipartFile.getContentType());
      } catch (IOException ioe) {
        throw new MultipartPictureException(String.format("Cannot parse multipart file : %s", multipartFile.getOriginalFilename()));
      }
    };
  }


  @PreAuthorize("hasAnyRole('" + ROLE_ADMIN + "')")
  @DeleteMapping
  public ResponseEntity<UUID> delete(@RequestParam("publicId") UUID id) {
    try {
      PublicId deletedProductUUID = productsApplicationService.deleteProduct(new PublicId(id));
      return ResponseEntity.ok(deletedProductUUID.value());
    } catch (EntityNotFoundException enfee) {
      log.error("Could not delete category with id {}", id, enfee);
      ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, enfee.getMessage());
      return ResponseEntity.of(problemDetail).build();
    }
  }


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
