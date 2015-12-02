package com.example.kanxuetest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SplashActivity extends Activity {

	private PackageInfo pinfo;
	private TextView versionText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = View
				.inflate(getApplicationContext(), R.layout.splash, null);
		setContentView(view);

		try {
			pinfo = getPackageManager().getPackageInfo(this.getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
			versionText = (TextView) findViewById(R.id.versionText_splash);
			versionText.setText(pinfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				gotoMainPage();
			}
		}).start();
	}
	
	/**
	 * 进入主页面
	 */
	private void gotoMainPage() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
