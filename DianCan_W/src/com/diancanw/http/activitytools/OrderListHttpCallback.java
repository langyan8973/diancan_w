package com.diancanw.http.activitytools;

import java.util.List;

import com.diancanw.model.Order;

public interface OrderListHttpCallback {
	public void RequestError(String errString);
	public void SetOrders(List<Order> orders);
	public void SetSelectOrder(Order order);
}
