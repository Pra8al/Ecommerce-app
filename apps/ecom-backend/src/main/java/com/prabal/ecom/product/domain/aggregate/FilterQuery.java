package com.prabal.ecom.product.domain.aggregate;

import com.prabal.ecom.product.domain.vo.ProductSize;
import com.prabal.ecom.product.domain.vo.PublicId;
import org.jilt.Builder;

import java.util.List;

@Builder
public record FilterQuery(PublicId categoryId, List<ProductSize> sizes) {
}
