package com.prabal.ecom.product.domain.aggregate;

import com.prabal.ecom.product.domain.vo.*;
import com.prabal.ecom.shared.error.domain.Assert;
import org.jilt.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public class Product {
  private final ProductBrand brand;
  private final ProductColor color;
  private final ProductDescription description;
  private final ProductName name;
  private final ProductPrice price;
  private final ProductSize size;
  private final Category category;
  private final List<Picture> pictures;
  private Long dbId;
  private boolean featured;
  private PublicId publicId;
  private int numberInStock;

  public Product(ProductBrand brand, ProductColor color, ProductDescription description, ProductName name, ProductPrice price, ProductSize size, Category category, List<Picture> pictures, Long dbId, boolean featured, PublicId publicId, int numberInStock) {
    assertMandatoryField(brand, color, description, name, price, size, category, pictures, featured, numberInStock);
    this.brand = brand;
    this.color = color;
    this.description = description;
    this.name = name;
    this.price = price;
    this.size = size;
    this.category = category;
    this.pictures = pictures;
    this.dbId = dbId;
    this.featured = featured;
    this.publicId = publicId;
    this.numberInStock = numberInStock;
  }

  private void assertMandatoryField(ProductBrand brand,
                                    ProductColor color,
                                    ProductDescription description,
                                    ProductName name,
                                    ProductPrice price,
                                    ProductSize size,
                                    Category category,
                                    List<Picture> pictures,
                                    boolean featured,
                                    int numberInStock) {
    Assert.notNull("brand", brand);
    Assert.notNull("color", color);
    Assert.notNull("description", description);
    Assert.notNull("name", name);
    Assert.notNull("price", price);
    Assert.notNull("size", size);
    Assert.notNull("category", category);
    Assert.notNull("pictures", pictures);
    Assert.notNull("featured", featured);
    Assert.notNull("numberInStock", numberInStock);
  }

  public void initDefaultFields() {
    this.publicId = new PublicId(UUID.randomUUID());
  }

  public ProductBrand getBrand() {
    return brand;
  }

  public ProductColor getColor() {
    return color;
  }

  public ProductDescription getDescription() {
    return description;
  }

  public ProductName getName() {
    return name;
  }

  public ProductPrice getPrice() {
    return price;
  }

  public ProductSize getSize() {
    return size;
  }

  public Category getCategory() {
    return category;
  }

  public List<Picture> getPictures() {
    return pictures;
  }

  public Long getDbId() {
    return dbId;
  }

  public boolean isFeatured() {
    return featured;
  }

  public PublicId getPublicId() {
    return publicId;
  }

  public int getNumberInStock() {
    return numberInStock;
  }
}
