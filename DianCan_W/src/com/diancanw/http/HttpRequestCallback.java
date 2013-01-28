package com.diancanw.http;

import android.os.Message;

public interface HttpRequestCallback {
	public void RequestComplete(Message msg);
	public void RequestError(String errString);
}
