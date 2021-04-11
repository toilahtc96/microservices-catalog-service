package com.example.microservices.controller;

import java.util.List;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.microservices.entities.Product;
import com.example.microservices.exceptions.ProductNotFoundException;
import com.example.microservices.services.ProductService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/products")
public class ProductController {
	private final ProductService productService;
    Logger logger = LoggerFactory.logger(ProductController.class);
 
	
	@Autowired
	public ProductController(ProductService productService) {
		this.productService = productService;
	}
	
	@GetMapping("")
	public List<Product> allProducts(){
		logger.info(productService.findAllProducts().get(0));
		return productService.findAllProducts();
	}
	
	@GetMapping("/{code}")
	public Product productByCode(@PathVariable String code) {
		return productService.findByCode(code).orElseThrow(
				()->new ProductNotFoundException("Product with code: [" + code + "] not found"));
	}

}
