package com.matulai.product_service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import org.junit.jupiter.api.Test;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

	@LocalServerPort
	private Integer port;

	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		// if the database is SQL or NoSQL the way to set the configuration changes.
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@BeforeAll
	static void beforeAll() {
		mongoDBContainer.start();
	}

	@AfterAll
	static void afterAll() {
		mongoDBContainer.stop();
	}

	// Rest template for HTTP communication from the logic app and RestAssured for testing.
	@Test
	void shouldCreateProduct() {
		String requestBody = """
								{
									"name": "iPhone 15",
									"description": "iPhone 15 is a smartphone from Apple",
									"price": 1000
								}
				""";
		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/api/product")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("iPhone 15"))
				.body("description", Matchers.equalTo("iPhone 15 is a smartphone from Apple"))
				.body("price", Matchers.equalTo(1000));
	}

}
