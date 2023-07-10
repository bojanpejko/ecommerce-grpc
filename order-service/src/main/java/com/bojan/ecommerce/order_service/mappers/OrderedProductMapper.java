package com.bojan.ecommerce.order_service.mappers;

import com.bojan.ecommerce.order.OrderedProductResponse;
import com.bojan.ecommerce.order_service.models.OrderedProduct;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderedProductMapper {

    OrderedProduct toEntity(OrderedProductResponse response);

    OrderedProductResponse toResponse(OrderedProduct entity);
}
