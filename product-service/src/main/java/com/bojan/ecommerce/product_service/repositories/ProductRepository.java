package com.bojan.ecommerce.product_service.repositories;

import com.bojan.ecommerce.product_service.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findProductByUid(String uid);
}
