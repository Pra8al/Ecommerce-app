package com.prabal.ecom.order.domain.order.aggregate;

import com.prabal.ecom.product.domain.vo.PublicId;
import org.jilt.Builder;

@Builder
public record DetailCartItemRequest(PublicId productId, long quantity) {

}
