package com.example.kanxuetest.net;

import com.example.kanxuetest.interfaces.NetClientinterface;

public class Api {

	public static final String DOMAIN = "http://bbs.pediy.com";
	public static final String PATH = "/";
	// 版块ID
	public static final int NEW_FORUM_ID = 153; // 新贴集合版块id
	public static final int SECURITY_FORUM_ID = 61; // 生活放心情版块id
	
	private static Api mInstance=null;
	
	public static Api getInstance(){
		if (mInstance==null) {
			mInstance=new Api();
		}
		return mInstance;
	}
	
	public void checkUpdate(NetClientinterface callback) {
		String url = DOMAIN + PATH + "mobile/android/appupdate.html";
		new HttpClientUtil(url, HttpClientUtil.METHOD_GET, callback)
				.asyncConn();
	}
}
