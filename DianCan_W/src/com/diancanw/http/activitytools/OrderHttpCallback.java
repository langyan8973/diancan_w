package com.diancanw.http.activitytools;

import com.diancanw.model.Order;

public interface OrderHttpCallback {
	public void RequestError(String errString);
	public void UpdateOrderPage(Order order);
	public void OrderChecked();
	public void OrderCancelled();
	public void UpdateOrderItemById(String idString);
}
