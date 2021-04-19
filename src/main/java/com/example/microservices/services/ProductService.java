package com.example.microservices.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esotericsoftware.minlog.Log;
import com.example.microservices.entities.Product;
import com.example.microservices.entities.response.ProductInventoryResponse;
import com.example.microservices.repositories.ProductRepository;
import com.example.microservices.utils.MyThreadLocalsHolder;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ProductService {
	private final ProductRepository productRepository;

	private final InventoryServiceClient inventoryServiceClient;

	@Autowired
	public ProductService(ProductRepository productRepository, InventoryServiceClient inventoryServiceClient) {
		this.productRepository = productRepository;
		this.inventoryServiceClient = inventoryServiceClient;
	}

	public List<Product> findAllProducts() {
		return productRepository.findAll();
	}

	public Optional<Product> findByCode(String code) {
		String correlationId = UUID.randomUUID().toString();
		MyThreadLocalsHolder.setCorrelationId(correlationId);
		Log.info("Before CorrelationID: " + MyThreadLocalsHolder.getCorrelationId());
		Optional<ProductInventoryResponse> responseEntity = inventoryServiceClient.getProductInventoryByCode(code);
		Log.info("After CorrelationID: " + MyThreadLocalsHolder.getCorrelationId());
		return productRepository.findByCode(code);
	}

}
