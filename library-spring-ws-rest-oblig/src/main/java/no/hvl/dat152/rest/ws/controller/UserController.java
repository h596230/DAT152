/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.service.UserService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/users")
	public ResponseEntity<Object> getUsers(){
		
		List<User> users = userService.findAllUsers();
		
		if(users.isEmpty())
			
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<>(users, HttpStatus.OK);
	}
	
	@GetMapping(value = "/users/{id}")
	public ResponseEntity<Object> getUser(@PathVariable("id") Long id) throws UserNotFoundException, OrderNotFoundException{
		
		User user = null;
		try {
			user = userService.findUser(id);
			addLinks(user.getOrders());
			
			return new ResponseEntity<>(user, HttpStatus.OK);
			
		}catch(UserNotFoundException e) {
			
			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}	
		
	}
	
	@PostMapping("/users")
	public ResponseEntity<Object> createUser(@RequestBody User user){
		
		user = userService.saveUser(user);
		
		return new ResponseEntity<>(user, HttpStatus.CREATED);
	}
	
	@PutMapping("/users/{id}")
	public ResponseEntity<Object> updateUser(@RequestBody User user, 
			@PathVariable("id") Long id) throws UserNotFoundException{
		
		user = userService.updateUser(user, id);
		
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Object> deleteUser(@PathVariable("id") Long id) throws UserNotFoundException{
		
		userService.deleteUser(id);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/users/{id}/orders")
	public ResponseEntity<Object> getUserOrders(@PathVariable("id") Long id) throws UserNotFoundException{
		
		Set<Order> orders = userService.findOrdersForUser(id);
		
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}
	
	@PostMapping("/users/{id}/orders")
	public ResponseEntity<Object> createUserOrders(@PathVariable("id") Long id, @RequestBody Order order) 
			throws UserNotFoundException, OrderNotFoundException{
		if(order == null){
			throw new OrderNotFoundException("Order details are missing");
		}
		userService.createOrdersForUser(id,order);
		Set<Order> orders = userService.findOrdersForUser(id);
		for(Order o: orders){
			//self
			o.add(linkTo(methodOn(OrderController.class).getBorrowOrder(o.getId())).withSelfRel());
			//cancel
			o.add(linkTo(methodOn(OrderController.class).returnBookOrder(o.getId())).withRel("cancel"));
			//update
			o.add(linkTo(methodOn(OrderController.class).updateOrder(o.getId(),order)).withRel("update"));
		}

		return new ResponseEntity<>(orders, HttpStatus.CREATED);
	}
	
	private void addLinks(Set<Order> orders) throws OrderNotFoundException {
		for(Order order : orders) {
			Link rordersLink = linkTo(methodOn(OrderController.class).returnBookOrder(order.getId())).withRel("Update_Return_or_Cancel");
			order.add(rordersLink);
		}
	}
	
}
