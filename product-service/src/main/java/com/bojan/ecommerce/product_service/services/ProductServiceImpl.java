package com.bojan.ecommerce.product_service.services;

import com.bojan.ecommerce.product.*;
import com.bojan.ecommerce.product_service.mappers.ProductMapper;
import com.bojan.ecommerce.product_service.models.Product;
import com.bojan.ecommerce.product_service.observers.CreateProductStreamObserver;
import com.bojan.ecommerce.product_service.repositories.ProductRepository;
import com.google.protobuf.Empty;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class ProductServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public void getProduct(GetProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Product product = productRepository.findProductByUid(request.getUid());
        responseObserver.onNext(productMapper.toResponse(product));
        responseObserver.onCompleted();
    }

    @Override
    public void getStock(ProductRequest request, StreamObserver<Int32Value> responseObserver) {
        Product product = productRepository.findProductByUid(request.getUid());
        try {
            while(!Thread.interrupted()){
                responseObserver.onNext(Int32Value.newBuilder().setValue(product.getLeftInStock()).build());
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            responseObserver.onError(e);
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<ProductRequest> createProducts(StreamObserver<Empty> responseObserver) {
        return new CreateProductStreamObserver(responseObserver, productRepository, productMapper);
    }

    @Override
    public StreamObserver<UpdateProductStockRequest> updateProductStock(StreamObserver<StringValue> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(UpdateProductStockRequest updateProductStockRequest) {
                Product product = productRepository.findProductByUid(updateProductStockRequest.getUid());
                product.setLeftInStock(product.getLeftInStock() - updateProductStockRequest.getQuantity());
                productRepository.save(product);
                responseObserver.onNext(StringValue.newBuilder().setValue("Product stock has been updated successfully!").build());
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(StringValue.newBuilder().setValue("All orders have been processed successfully").build());
                responseObserver.onCompleted();
            }
        };
    }
}
