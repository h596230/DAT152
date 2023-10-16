package no.hvl.dat152.rest.ws.main.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.service.UserService;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TestUser {
	
	@Autowired
	private UserService userService;

	private String API_ROOT = "http://localhost:8090/elibrary/api/v1";
	
	@DisplayName("JUnit test for @GetMapping(/users) endpoint")
	@Test
	public void getAllUsers_thenOK() {
		Response response = RestAssured.get(API_ROOT+"/users");
		assertEquals(HttpStatus.OK.value(), response.getStatusCode());
		assertTrue(response.jsonPath().getList("userid").size() > 0);
	}
	
	@DisplayName("JUnit test for @GetMapping(/users/{id}) endpoint")
	@Test
	public void getUserById_thenOK() {

	    Response response = RestAssured.get(API_ROOT+"/users/1");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals("1", response.jsonPath().get("userid").toString());

	}
	
	@DisplayName("JUnit test for @PostMapping(/users) endpoint")
	@Test
	public void createUser_thenOK() {
		
		User user1 = new User("Test1", "User1");
		
		Response response = RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(user1)
				.post(API_ROOT+"/users");
	    
	    assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
	    assertEquals("Test1", response.jsonPath().get("firstname"));

	}
	
	@DisplayName("JUnit test for @GetMapping(/users/{id}/orders) endpoint")
	@Test
	public void getOrdersOfUser_thenOK() {
		
		Response response = RestAssured.get(API_ROOT+"/users/1/orders");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertTrue(response.jsonPath().getList("isbn").size() > 0);
	}
	
	@DisplayName("JUnit test for @PutMapping(/users/{id}) endpoint")
	@Test
	public void updateUser_thenOK() {
		User user1 = new User("Test1", "User1");
		user1 = userService.saveUser(user1);
		
		User up_user1 = new User("Test1_up", "User1_up");
		up_user1.setUserid(user1.getUserid());
		
		Response response = RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(up_user1)
				.put(API_ROOT+"/users/{id}", up_user1.getUserid());
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals("Test1_up", response.jsonPath().get("firstname"));
	}

	@DisplayName("JUnit test for @PostMapping(/users/{id}/orders) endpoint")
	@Test
	public void createOrderForUser_thenOK() throws UserNotFoundException {

		// new order
		Order order1 = new Order("qabfde1230", LocalDate.now().plusWeeks(2));	
		
		Response response = RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(order1)
				.post(API_ROOT+"/users/{id}/orders", "4");

	    assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
	    assertTrue(response.jsonPath().getList("orders").get(0).toString().contains("qabfde1230"));

	}
	
	@DisplayName("JUnit test for @DeleteMapping(/users/{id}) endpoint")
	@Test
	public void deleteUserById_thenOK() {

	    Response response = RestAssured.delete(API_ROOT+"/users/1");
	    
	    assertEquals(HttpStatus.OK.value(), response.getStatusCode());
	    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), RestAssured.get(API_ROOT+"/users/1").getStatusCode());

	}

}
