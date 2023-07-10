package com.bojan.ecommerce.product_service.observers;

import com.bojan.ecommerce.product.ProductRequest;
import com.bojan.ecommerce.product_service.mappers.ProductMapper;
import com.bojan.ecommerce.product_service.repositories.ProductRepository;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateProductStreamObserver implements StreamObserver<ProductRequest> {

    private final StreamObserver<Empty> responseObserver;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public void onNext(ProductRequest productRequest) {
        productRepository.save(productMapper.toEntity(productRequest));
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }
}
