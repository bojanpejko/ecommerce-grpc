package com.bojan.ecommerce.order_service.services;

import com.bojan.ecommerce.order.*;
import com.bojan.ecommerce.order_service.mappers.OrderMapper;
import com.bojan.ecommerce.order_service.models.Order;
import com.bojan.ecommerce.order_service.repositories.OrderRepository;
import com.bojan.ecommerce.product.ProductServiceGrpc;
import com.bojan.ecommerce.product.UpdateProductStockRequest;
import com.google.protobuf.StringValue;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @GrpcClient("product-service-client")
    private final ProductServiceGrpc.ProductServiceStub productServiceStub;

    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        StreamObserver<UpdateProductStockRequest> requestStreamObserver = productServiceStub.updateProductStock(new StreamObserver<>() {
            @Override
            public void onNext(StringValue stringValue) {
                log.info("Got {} from server", stringValue.getValue());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("There was an error updating the product stock: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                log.info("Product stock update has been completed!");
            }
        });

        request
            .getOrderedProductsList()
            .forEach(
                    orderedProduct -> {
                        try {
                            requestStreamObserver.onNext(
                                UpdateProductStockRequest.newBuilder()
                                    .setUid(orderedProduct.getProductUid())
                                    .setQuantity(orderedProduct.getQuantity())
                                    .build()
                            );
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            requestStreamObserver.onError(e);
                            e.printStackTrace();
                        }
                    }
            );
        requestStreamObserver.onCompleted();

        Order order = orderRepository.save(orderMapper.toEntity(request));
        responseObserver.onNext(orderMapper.toResponse(order));
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void listCustomerOrderHistory(ListCustomerOrderHistoryRequest request, StreamObserver<OrderResponse> responseObserver) {
        orderRepository.findOrdersByCustomerIdOrderByUpdatedAt(request.getCustomerId())
            .stream()
            .map(orderMapper::toResponse)
            .forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }
}
