package com.example.microservices.controller;

import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.microservices.entities.Product;
import com.example.microservices.entities.response.ProductInventoryResponse;
import com.example.microservices.exceptions.ProductNotFoundException;
import com.example.microservices.services.InventoryServiceClient;
import com.example.microservices.services.ProductService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/products")
public class ProductController {
	private final ProductService productService;
	private final InventoryServiceClient inventoryServiceClient;
	Logger logger = LoggerFactory.logger(ProductController.class);

	@Autowired
	public ProductController(ProductService productService, InventoryServiceClient inventoryServiceClient) {
		this.productService = productService;
		this.inventoryServiceClient = inventoryServiceClient;
	}

	@GetMapping("")
	public List<Product> allProducts() {
		return productService.findAllProducts();
	}

	@GetMapping("/{code}")
	public Product productByCode(@PathVariable String code) {
		return productService.findByCode(code)
				.orElseThrow(() -> new ProductNotFoundException("Product with code: [" + code + "] not found"));
	}

	@GetMapping("/inventory/{code}")
	public Optional<ProductInventoryResponse> getProductInventoryByCode(@PathVariable String code) {
		return inventoryServiceClient.getProductInventoryByCode(code);
	}

}
