package com.bojan.ecommerce.order_service.mappers;

import com.bojan.ecommerce.order.CreateOrderRequest;
import com.bojan.ecommerce.order.OrderResponse;
import com.bojan.ecommerce.order_service.models.Order;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = OrderedProductMapper.class,
    imports = UUID.class,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface OrderMapper {

    @Mapping(target = "uid", expression = "java(UUID.randomUUID().toString())")
    @Mapping(source = "orderedProductsList", target = "orderedProducts")
    Order toEntity(CreateOrderRequest request);

    @Mapping(source = "orderedProducts", target = "orderedProductsList")
    OrderResponse toResponse(Order entity);
}
