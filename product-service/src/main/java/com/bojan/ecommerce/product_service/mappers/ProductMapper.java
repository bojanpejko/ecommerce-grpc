package com.bojan.ecommerce.product_service.mappers;

import com.bojan.ecommerce.product.ProductRequest;
import com.bojan.ecommerce.product.ProductResponse;
import com.bojan.ecommerce.product_service.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = UUID.class
)
public interface ProductMapper {

    @Mapping(target = "uid", expression = "java(UUID.randomUUID().toString())")
    Product toEntity(ProductRequest request);

    ProductResponse toResponse(Product entity);
}
