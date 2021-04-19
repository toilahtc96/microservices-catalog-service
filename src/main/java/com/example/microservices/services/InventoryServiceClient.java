package com.example.microservices.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import com.esotericsoftware.minlog.Log;
import com.example.microservices.entities.response.ProductInventoryResponse;
import com.example.microservices.utils.MyThreadLocalsHolder;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@Service
@Slf4j
public class InventoryServiceClient {
	private final RestTemplate restTemplate;

	@Autowired
	public InventoryServiceClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;

	}

	@HystrixCommand(fallbackMethod = "getDefaultProductInventory", commandProperties = {
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
			@HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60"),
			@HystrixProperty(name="execution.isolation.strategy", value="SEMAPHORE")})
	public Optional<ProductInventoryResponse> getProductInventoryByCode(String code) {
		Log.info("CorrelationID: "+ MyThreadLocalsHolder.getCorrelationId());
		ResponseEntity<ProductInventoryResponse> itemResponseEntity = restTemplate
				.getForEntity("http://inventory-service/api/inventory/{code}", ProductInventoryResponse.class, code);
		if (itemResponseEntity.getStatusCode() == HttpStatus.OK) {
			return Optional.ofNullable(itemResponseEntity.getBody());
		} else {
			Log.error("Unable to call inventory service with code: " + code + " Status"
					+ itemResponseEntity.getStatusCode() + " !");
			return Optional.empty();
		}
	}

	Optional<ProductInventoryResponse> getDefaultProductInventory(String code) {
		Log.info("Return default for get ProductInventory because fall to connect service");
		ProductInventoryResponse response = new ProductInventoryResponse();
		response.setAvailableQuantity(0);
		response.setProductCode(null);
		return Optional.ofNullable(response);
	}

}
