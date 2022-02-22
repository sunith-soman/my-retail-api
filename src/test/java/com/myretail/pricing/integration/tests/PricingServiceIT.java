package com.myretail.pricing.integration.tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PricingServiceIT {

	@Autowired
	private WebTestClient webTestClient;

	private static WireMockServer wireMockServer;

	@BeforeAll
	public static void init() {
		setupWireMockServer();
	}

	private static void setupWireMockServer() {
		wireMockServer = new WireMockServer(wireMockConfig().port(9999));
		configureFor("localhost",9999);
		wireMockServer.start();

		stubFor(
				get(urlEqualTo("/redsky_aggregations/v1/redsky/case_study_v1?key=t1r3h2nn&tcin=13860428"))
						.willReturn(aResponse().withStatus(200).withBodyFile("productResponse13860428.json"))
		);

		stubFor(
				get(urlEqualTo("/redsky_aggregations/v1/redsky/case_study_v1?key=t1r3h2nn&tcin=12954218"))
						.willReturn(aResponse().withStatus(200).withBodyFile("productResponse12954218.json"))
		);

		stubFor(
				get(urlEqualTo("/redsky_aggregations/v1/redsky/case_study_v1?key=t1r3h2nn&tcin=54456119"))
						.willReturn(aResponse().withStatus(429))
		);
	}

	@Test
	public void getProductPrice_SuccessScenario() throws Exception {
		webTestClient.get().uri("/price/products/13860428")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization","Basic Y29uc3VtZXIxOnBhc3N3b3JkMQ==")
				.exchange().expectStatus().isOk()
				.expectBody()
				.jsonPath("id").isEqualTo(13860428)
				.jsonPath("name").isEqualTo("The Big Lebowski (Blu-ray)")
				.jsonPath("current_price.value").isEqualTo(13.21)
				.jsonPath("current_price.currency_code").isEqualTo("USD");
	}

	@Test
	public void getProductPrice_ProductNameServiceErrorScenario() throws Exception {
		webTestClient.get().uri("/price/products/54456119")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization","Basic Y29uc3VtZXIxOnBhc3N3b3JkMQ==")
				.exchange().expectStatus().isOk()
				.expectBody()
				.jsonPath("id").isEqualTo(54456119)
				.jsonPath("name").isEqualTo("Not Available")
				.jsonPath("current_price.value").isEqualTo(21.56)
				.jsonPath("current_price.currency_code").isEqualTo("USD");
	}

	@Test
	public void getProductPrice_BadRequestScenario() throws Exception {
		webTestClient.get().uri("/price/products/abcd")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization","Basic Y29uc3VtZXIxOnBhc3N3b3JkMQ==")
				.exchange().expectStatus().isBadRequest();
	}

	@Test
	public void updateProductPrice_SuccessScenario() throws Exception {
		String payload = IOUtils.toString(this.getClass().getResourceAsStream("/json/updatePrice.json"),"UTF-8");
		webTestClient.put().uri("/price/products/12954218")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization","Basic Y29uc3VtZXIxOnBhc3N3b3JkMQ==")
				.body(BodyInserters.fromValue(payload)).exchange().expectStatus().isOk()
				.expectBody()
				.jsonPath("id").isEqualTo(12954218)
				.jsonPath("name").isEqualTo("Kraft Macaroni &#38; Cheese Dinner Original - 7.25oz")
				.jsonPath("current_price.value").isEqualTo(33.34)
				.jsonPath("current_price.currency_code").isEqualTo("USD");

		webTestClient.get().uri("/price/products/12954218")
				.accept(MediaType.APPLICATION_JSON)
				.header("Authorization","Basic Y29uc3VtZXIxOnBhc3N3b3JkMQ==")
				.exchange().expectStatus().isOk()
				.expectBody()
				.jsonPath("id").isEqualTo(12954218)
				.jsonPath("current_price.value").isEqualTo(33.34);
	}

	@Test
	public void updateProductPrice_BadRequest() throws Exception {
		String payload = IOUtils.toString(this.getClass().getResourceAsStream("/json/updatePriceWithZero.json"),"UTF-8");
		webTestClient.put().uri("/price/products/12954218")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization","Basic Y29uc3VtZXIxOnBhc3N3b3JkMQ==")
				.body(BodyInserters.fromValue(payload)).exchange().expectStatus().isBadRequest();
	}
}
