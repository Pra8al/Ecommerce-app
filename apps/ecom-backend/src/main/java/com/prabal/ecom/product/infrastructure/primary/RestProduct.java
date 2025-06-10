package com.prabal.ecom.product.infrastructure.primary;

import com.prabal.ecom.product.domain.aggregate.Product;
import com.prabal.ecom.product.domain.aggregate.ProductBuilder;
import com.prabal.ecom.product.domain.vo.*;
import org.jilt.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public class RestProduct {

  private String brand;
  private String color;
  private String description;
  private String name;
  private double price;
  private ProductSize size;
  private RestCategory category;
  private boolean featured;
  private List<RestPicture> pictures;
  private UUID publicId;
  private int numberInStock;

  public RestProduct() {
  }

  public RestProduct(String brand, String color, String description,
                     String name, double price, ProductSize size,
                     RestCategory category, boolean featured,
                     List<RestPicture> pictures, UUID publicId, int numberInStock) {
    this.brand = brand;
    this.color = color;
    this.description = description;
    this.name = name;
    this.price = price;
    this.size = size;
    this.category = category;
    this.featured = featured;
    this.pictures = pictures;
    this.publicId = publicId;
    this.numberInStock = numberInStock;
  }

  public void addPictureAttachment(List<RestPicture> pictures) {
    this.pictures.addAll(pictures);
  }

  public static Product toDomain(RestProduct restProduct) {
    ProductBuilder productBuilder = ProductBuilder.product()
      .brand(new ProductBrand(restProduct.getBrand()))
      .color(new ProductColor(restProduct.getColor()))
      .description(new ProductDescription(restProduct.getDescription()))
      .name(new ProductName(restProduct.getName()))
      .price(new ProductPrice(restProduct.getPrice()))
      .size(restProduct.getSize())
      .category(RestCategory.toDomain(restProduct.getCategory()))
      .featured(restProduct.isFeatured())
      .numberInStock(restProduct.getNumberInStock());

    if (restProduct.publicId != null) {
      productBuilder.publicId(new PublicId(restProduct.publicId));
    }

    if (restProduct.pictures != null && !restProduct.pictures.isEmpty()) {
      productBuilder.pictures(RestPicture.toDomain(restProduct.getPictures()));
    }

    return productBuilder.build();
  }

  public static RestProduct fromDomain(Product product) {
    return RestProductBuilder.restProduct()
      .brand(product.getBrand().value())
      .color(product.getColor().value())
      .description(product.getDescription().value())
      .name(product.getName().value())
      .price(product.getPrice().value())
      .featured(product.isFeatured())
      .category(RestCategory.fromDomain(product.getCategory()))
      .size(product.getSize())
      .pictures(RestPicture.fromDomain(product.getPictures()))
      .publicId(product.getPublicId().value())
      .numberInStock(product.getNumberInStock())
      .build();
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public ProductSize getSize() {
    return size;
  }

  public void setSize(ProductSize size) {
    this.size = size;
  }

  public RestCategory getCategory() {
    return category;
  }

  public void setCategory(RestCategory category) {
    this.category = category;
  }

  public boolean isFeatured() {
    return featured;
  }

  public void setFeatured(boolean featured) {
    this.featured = featured;
  }

  public List<RestPicture> getPictures() {
    return pictures;
  }

  public void setPictures(List<RestPicture> pictures) {
    this.pictures = pictures;
  }

  public UUID getPublicId() {
    return publicId;
  }

  public void setPublicId(UUID publicId) {
    this.publicId = publicId;
  }

  public int getNumberInStock() {
    return numberInStock;
  }

  public void setNumberInStock(int numberInStock) {
    this.numberInStock = numberInStock;
  }
}
