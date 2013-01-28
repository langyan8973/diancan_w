package com.diancanw.http.activitytools;

import java.util.List;

import com.diancanw.model.Desk;
import com.diancanw.model.DeskType;
import com.diancanw.model.Order;

public interface DeskListHttpCallback {
	public void RequestError(String errString);
	public void SetDeskTypes(List<DeskType> deskTypes);
	public void SetDeskList(List<Desk> desks);
	public void SetNewOrder(Order order);
	public void SetOrder(Order order);
}
