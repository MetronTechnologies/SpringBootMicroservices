package com.microservices.productservices.Repository;

import com.microservices.productservices.Model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {



}
