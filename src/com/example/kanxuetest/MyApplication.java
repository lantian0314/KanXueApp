package com.example.kanxuetest;

import java.util.List;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.kanxuetest.interfaces.CheckUpdateInterface;
import com.example.kanxuetest.interfaces.NetClientinterface;
import com.example.kanxuetest.net.Api;
import com.example.kanxuetest.net.HttpClientUtil;

import android.app.Application;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

public class MyApplication extends Application {

	private int m_versionCode;

	@Override
	public void onCreate() {
		super.onCreate();
		PackageInfo pinfo = null;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
			m_versionCode = pinfo.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查更新
	 * 
	 * @param context
	 */
	public void checkUpdate(Context context) {
		chechUpdate(context, null);
	}

	private void chechUpdate(final Context context,
			final CheckUpdateInterface callback) {
		Api.getInstance().checkUpdate(new NetClientinterface() {
			@Override
			public void execute(int status, String jsonData,
					List<Cookie> cookies) {
				if (callback != null) {
					callback.networkComplete();
				}
				if (status != HttpClientUtil.NET_SUCCESS) {
					return;
				}
				try {
					final JSONObject	jsonObject = new JSONObject(jsonData);
					if (jsonObject.optInt("version") == m_versionCode) {
						if (callback != null) {
							callback.noUpdate();
						}
						return;
					}
					
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							Builder builder = new Builder(context);
							builder.setTitle("检测到新版本");
							String versionName=versionCodeToName(jsonObject.optInt("version"));
							String size=converToSuitableSize(jsonObject.optInt("size"));
							builder.setMessage("版本"+versionName+"\n大小:"+size+"\n是否更新？");
							builder.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}
							});
							builder.setNegativeButton("取消", null);
							builder.create().show();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private String versionCodeToName(int code) {
		return code / 100 + "." + code / 10 % 10 + "." + code % 100;
	}
	
	private String converToSuitableSize(int size) {
		if (size >= 1024) {
			return (size / 1024.0 + "").substring(0, 3) + "MB";
		}
		return size + "KB";
	}
	
	private Handler mHandler = new Handler();
}
