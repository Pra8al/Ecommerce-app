package com.prabal.ecom.product.domain.aggregate;

import com.prabal.ecom.product.domain.vo.ProductBrand;
import com.prabal.ecom.product.domain.vo.ProductName;
import com.prabal.ecom.product.domain.vo.ProductPrice;
import com.prabal.ecom.product.domain.vo.PublicId;
import com.prabal.ecom.shared.error.domain.Assert;
import org.jilt.Builder;

@Builder
public class ProductCart {

  private ProductName name;

  private ProductPrice price;

  private ProductBrand brand;

  private Picture picture;

  private PublicId publicId;

  public ProductCart(ProductName name, ProductPrice price, ProductBrand brand, Picture picture, PublicId publicId) {
    assertFields(name, price, brand, picture, publicId);
    this.name = name;
    this.price = price;
    this.brand = brand;
    this.picture = picture;
    this.publicId = publicId;
  }

  private void assertFields(ProductName name, ProductPrice price, ProductBrand brand, Picture picture, PublicId publicId) {
    Assert.notNull("name", name);
    Assert.notNull("price", price);
    Assert.notNull("brand", brand);
    Assert.notNull("picture", picture);
    Assert.notNull("publicId", publicId);
  }

  public static ProductCart from(Product product) {
    return ProductCartBuilder.productCart()
      .name(product.getName())
      .price(product.getPrice())
      .brand(product.getBrand())
      .picture(product.getPictures().stream().findFirst().orElseThrow())
      .publicId(product.getPublicId())
      .build();
  }

  public ProductName getName() {
    return name;
  }

  public void setName(ProductName name) {
    this.name = name;
  }

  public ProductPrice getPrice() {
    return price;
  }

  public void setPrice(ProductPrice price) {
    this.price = price;
  }

  public ProductBrand getBrand() {
    return brand;
  }

  public void setBrand(ProductBrand brand) {
    this.brand = brand;
  }

  public Picture getPicture() {
    return picture;
  }

  public void setPicture(Picture picture) {
    this.picture = picture;
  }

  public PublicId getPublicId() {
    return publicId;
  }

  public void setPublicId(PublicId publicId) {
    this.publicId = publicId;
  }
}
