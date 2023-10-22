/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class OrderController {

	@Autowired
	private OrderService orderService;

	
	@GetMapping("/orders")
	public ResponseEntity<PagedModel<Order>> getAllBorrowOrders(
			@RequestParam(required = false) LocalDate expiry, 
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "1") int size){

		Page<Order> orders;
		if(expiry == null){
			orders = orderService.findAllOrders(PageRequest.of(page, size));
		}else {
			orders = orderService.findByExpiryDate(expiry,PageRequest.of(page,size));
		}
		if(orders.isEmpty()){
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(orders.getSize(), orders.getNumber(),orders.getTotalElements(), orders.getTotalPages());
		PagedModel<Order> pagedModel  = PagedModel.of(orders.getContent(),metadata);
		//sadly link look like this http://localhost:8090/elibrary/api/v1/orders?page=1&size=1{&expiry}, I don't know how to change it
		if(orders.hasNext()){
			pagedModel.add(linkTo(methodOn(OrderController.class).getAllBorrowOrders(expiry, orders.nextPageable().getPageNumber(),size)).withRel("next"));
		}
		if(orders.hasPrevious()){
			pagedModel.add(linkTo(methodOn(OrderController.class).getAllBorrowOrders(expiry, orders.previousPageable().getPageNumber(),size)).withRel("previous"));
		}
		return new ResponseEntity<>(pagedModel,HttpStatus.OK);
	}
	
	@GetMapping("/orders/{id}")
	public ResponseEntity<Object> getBorrowOrder(@PathVariable("id") Long id) throws OrderNotFoundException{
		
		try {
			Order order = orderService.findOrder(id);
			//a link to self
			order.add(linkTo(methodOn(OrderController.class).getBorrowOrder(id)).withSelfRel());
			// cancel
			order.add(linkTo(methodOn(OrderController.class).returnBookOrder(id)).withRel("cancel"));
			return new ResponseEntity<>(order, HttpStatus.OK);
			
		}catch(OrderNotFoundException e) {

			return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}	
		
	}
	
	@PutMapping("/orders/{id}")
	public ResponseEntity<Object> updateOrder(@PathVariable("id") Long id, @RequestBody Order order) 
			throws OrderNotFoundException, UserNotFoundException{

		order = orderService.updateOrder(order, id);
		Link rordersLink = linkTo(methodOn(OrderController.class).returnBookOrder(id)).withRel("Update_Return_or_Cancel");
		order.add(rordersLink);
		
		return new ResponseEntity<>(order, HttpStatus.OK);
	}
	
	@DeleteMapping("/orders/{id}")
	public ResponseEntity<Object> returnBookOrder(@PathVariable("id") Long id) throws OrderNotFoundException {
		//Exception is thrown by findOrder(id) used inside the deleteOrder
		orderService.deleteOrder(id);
		return new ResponseEntity<>("Order with id: '" + id + "' deleted!", HttpStatus.OK);
	}
}
