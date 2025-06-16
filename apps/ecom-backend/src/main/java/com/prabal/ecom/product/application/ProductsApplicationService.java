package com.prabal.ecom.product.application;

import com.prabal.ecom.order.domain.order.aggregate.OrderProductQuantity;
import com.prabal.ecom.product.domain.aggregate.Category;
import com.prabal.ecom.product.domain.aggregate.FilterQuery;
import com.prabal.ecom.product.domain.aggregate.Product;
import com.prabal.ecom.product.domain.repository.CategoryRepository;
import com.prabal.ecom.product.domain.repository.ProductRepository;
import com.prabal.ecom.product.domain.service.CategoryCRUD;
import com.prabal.ecom.product.domain.service.ProductCRUD;
import com.prabal.ecom.product.domain.service.ProductShop;
import com.prabal.ecom.product.domain.service.ProductUpdater;
import com.prabal.ecom.product.domain.vo.PublicId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsApplicationService {

  private final ProductCRUD productCRUD;
  private final CategoryCRUD categoryCRUD;
  private final ProductShop productShop;
  private final ProductUpdater productUpdater;

  public ProductsApplicationService(ProductRepository productRepository, CategoryRepository categoryRepository) {
    this.productCRUD = new ProductCRUD(productRepository);
    this.categoryCRUD = new CategoryCRUD(categoryRepository);
    this.productShop = new ProductShop(productRepository);
    this.productUpdater = new ProductUpdater(productRepository);
  }

  @Transactional
  public Product createProduct(Product newProduct) {
    return productCRUD.save(newProduct);
  }

  @Transactional(readOnly = true)
  public Page<Product> findAllProduct(Pageable pageable) {
    return productCRUD.findAll(pageable);
  }

  @Transactional
  public PublicId deleteProduct(PublicId publicId) {
    return productCRUD.remove(publicId);
  }

  @Transactional
  public Category createCategory(Category category) {
    return categoryCRUD.save(category);
  }

  @Transactional
  public PublicId deleteCategory(PublicId publicId) {
    return categoryCRUD.delete(publicId);
  }

  @Transactional(readOnly = true)
  public Page<Category> findAllCategory(Pageable pageable) {
    return categoryCRUD.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public Page<Product> getFeaturedProduct(Pageable pageable) {
    return productShop.getFeaturedProducts(pageable);
  }

  @Transactional(readOnly = true)
  public Optional<Product> findOne(PublicId id) {
    return productCRUD.findOne(id);
  }

  @Transactional(readOnly = true)
  public Page<Product> findRelatedProducts(Pageable pageable, PublicId productPublicId) {
    return productShop.findRelated(pageable, productPublicId);
  }

  @Transactional(readOnly = true)
  public Page<Product> filter(Pageable pageable, FilterQuery filterQuery) {
    return productShop.filter(pageable, filterQuery);
  }

  @Transactional(readOnly = true)
  public List<Product> getProductByPublicIdsIn(List<PublicId> publicIds){
    return productCRUD.findAllByPublicIdIn(publicIds);
  }

  @Transactional
  public void updateProductQuantity(List<OrderProductQuantity> orderProductQuantities){
      productUpdater.updateProductQuantity(orderProductQuantities);
  }

}
