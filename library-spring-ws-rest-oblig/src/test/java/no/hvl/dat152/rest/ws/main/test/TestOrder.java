package no.hvl.dat152.rest.ws.main.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TestOrder {
	
	private String API_ROOT = "http://localhost:8090/elibrary/api/v1";
	
	@DisplayName("JUnit test for @GetMapping(/orders) endpoint")
	@Test
	public void getAllOrders_thenOK() {
		Response response = RestAssured.given()
				.param("expiry", "2023-11-30")
				.param("page", 0)
				.param("size", 4)
				.get(API_ROOT+"/orders");
		
		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertTrue(response.jsonPath().getList("isbn").size() > 0);
	}
	
	@DisplayName("JUnit test for @GetMapping(/orders/{id}) endpoint")
	@Test
	public void getOrderById_thenOK() {

	    Response response = RestAssured.get(API_ROOT+"/orders/2");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals("abcde1234", response.jsonPath().get("isbn"));
	}
	
	@DisplayName("JUnit test for @PutMapping(/orders/{id}) endpoint")
	@Test
	public void updateOrder_thenOK() throws OrderNotFoundException {
	
//		Order order = orderService.findOrder(1L);
		
		Order order1 = new Order("abcde1234", LocalDate.now().plusWeeks(4));
		order1.setId(2L);
		
		Response response = RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(order1)
				.put(API_ROOT+"/orders/{id}", order1.getId());
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals("abcde1234", response.jsonPath().get("isbn"));
	}
	
	@DisplayName("JUnit test for @DeleteMapping(/orders/{id}) endpoint")
	@Test
	public void deleteOrderById_thenOK() {

	    Response response = RestAssured.delete(API_ROOT+"/orders/1");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), RestAssured.get(API_ROOT+"/orders/1").getStatusCode());

	}

}
