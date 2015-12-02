package com.example.kanxuetest;

import com.example.kanxuetest.net.Api;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	private String[] m_tabTitle = new String[] { "新贴", "主页", "安全资讯", "设置" };

	private Class<?>[] m_tabIntent = new Class<?>[] { DisplayPage.class,
			HomePage.class, DisplayPage.class, SettingPage.class };

	private Bundle[] bundles = new Bundle[] {
			createBundle(Api.NEW_FORUM_ID, "新帖", true), null,
			createBundle(Api.SECURITY_FORUM_ID, "安全资讯", true), null };

	private int[] m_tabIcon = new int[] { R.drawable.collections_view_as_list,
			R.drawable.collections_view_as_grid, R.drawable.coffee,
			R.drawable.action_settings };
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TabHost tabHost = getTabHost();
		for (int i = 0; i < m_tabTitle.length; i++) {
			String title = m_tabTitle[i];
			Intent intent = new Intent(getApplicationContext(), m_tabIntent[i]);
			if (bundles[i] != null) {
				intent.putExtras(bundles[i]);
			}

			View tab = getLayoutInflater().inflate(R.layout.tab_iteam, null);
			ImageView tab_image = (ImageView) tab.findViewById(R.id.tabIcon);
			tab_image.setImageResource(m_tabIcon[i]);

			TabSpec spec = tabHost.newTabSpec(title).setIndicator(tab)
					.setContent(intent);
			tabHost.addTab(spec);
		}

		// 检查更新
		MyApplication application = (MyApplication) this.getApplication();
		application.checkUpdate(this);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (count == 1) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
				return true;
			}
			count++;
			Toast.makeText(getApplicationContext(), "再按一次退出", 0).show();
		}
		return false;
	}

	private Bundle createBundle(int id, String title, boolean isHideBackBtn) {
		Bundle bundles = new Bundle();
		bundles.putInt("id", id);
		bundles.putString("title", title);
		bundles.putBoolean("isHideBackBtn", isHideBackBtn);
		return bundles;
	}
}
