/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;
import java.util.Objects;

import no.hvl.dat152.rest.ws.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.repository.OrderRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author tdoy
 */
@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	public Order saveOrder(Order order) {
		
		order = orderRepository.save(order);
		
		return order;
	}
	
	public void deleteOrder(Long id) throws OrderNotFoundException {
        orderRepository.delete(findOrder(id));
    }

	@Transactional
	public Page<Order> findAllOrders(Pageable pageable){
		return orderRepository.findAll(pageable);
	}
	
	
	public Page<Order> findByExpiryDate(LocalDate expiry, Pageable page){
		return orderRepository.findByExpiryBefore(expiry,page);
	}
	
	public Order findOrder(Long id) throws OrderNotFoundException {
		
		return orderRepository.findById(id)
				.orElseThrow(()-> new OrderNotFoundException("Order with id: "+id+" not found in the order list!"));
	}
	
	public Order updateOrder(Order order, Long id) throws OrderNotFoundException {
		Order oldOrder = findOrder(id);
		boolean updated = false;
		//I could use Reflections to loop through Order
		if(!Objects.equals(oldOrder.getIsbn(),order.getIsbn())){
			oldOrder.setIsbn(order.getIsbn());
			updated = true;
		}
		if(!Objects.equals(oldOrder.getExpiry(),order.getExpiry())){
			oldOrder.setExpiry(order.getExpiry());
			updated = true;
		}

		if(updated){
			orderRepository.save(oldOrder);
		}
		return oldOrder;
	}
}
