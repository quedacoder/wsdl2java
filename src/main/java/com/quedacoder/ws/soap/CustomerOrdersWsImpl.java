package com.quedacoder.ws.soap;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.feature.Features;

@Features(features = "org.apache.cxf.feature.LoggingFeature")
public class CustomerOrdersWsImpl implements CustomerOrdersPortType {

	Map<BigInteger, List<Order>> customerOrders = new HashMap<>();

	int currentId;

	public CustomerOrdersWsImpl() {
		init();
	}

	public void init() {
		List<Order> orders = new ArrayList<>();

		Order order = new Order();
		order.setId(BigInteger.valueOf(1));

		Product product = new Product();
		product.setId("1");
		product.setDescription("IPhone");
		product.setQuantity(BigInteger.valueOf(3));
		order.getProduct().add(product);

		orders.add(order);

		customerOrders.put(BigInteger.valueOf(++currentId), orders);

	}

	@Override
	public GetOrdersResponse getOrders(GetOrdersRequest request) {

		BigInteger customerId = request.getCustomerId();
		List<Order> orders = customerOrders.get(customerId);

		GetOrdersResponse response = new GetOrdersResponse();
		response.getOrder().addAll(orders);

		return response;
	}

	@Override
	public CreateOrdersResponse createOrders(CreateOrdersRequest request) {

		BigInteger customerId = request.getCustomerId();
		Order order = request.getOrder();

		List<Order> orders = new ArrayList<>();

		// check to see if the customer has orders
		if (customerOrders.containsKey(customerId)) {
			orders = customerOrders.get(customerId);
		}

		orders.add(order);
		customerOrders.put(customerId, orders);

		// get the orders response
		CreateOrdersResponse response = new CreateOrdersResponse();
		response.setResult(true);

		return response;
	}

	@Override
	public DeleteCustomerOrderResponse deleteOrder(DeleteCustomerOrderRequest request) {

		// get parameters from the request
		BigInteger customerId = request.getCustomerId();
		BigInteger orderId = request.getOrderId();
		String message = "";

		// get the list of order for the given customer
		List<Order> orders = customerOrders.get(customerId);

		// get the order info
		if (orders != null) {
			Order order = orders.stream().filter(custOrder -> orderId.equals(custOrder.getId())).findAny().orElse(null);
			
			if (order != null) {
				orders.remove(order);
				message = "Order " + order.getId() + " was deleted successfully";
			}
			
		} else {
			// Order not found 
			message = "Order " + orderId + " was not deleted. Could not find order in database.";
		}

		DeleteCustomerOrderResponse response = new DeleteCustomerOrderResponse();
		response.setMessage(message);

		return response;
	}

}
