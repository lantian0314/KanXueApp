package com.example.kanxuetest.interfaces;

import java.util.List;

import org.apache.http.cookie.Cookie;

public interface NetClientinterface {

	public abstract void execute(int status, String jsonData,
			List<Cookie> cookies);
}
