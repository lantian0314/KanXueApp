package com.example.kanxuetest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.kanxuetest.interfaces.CheckUpdateInterface;
import com.example.kanxuetest.interfaces.NetClientinterface;
import com.example.kanxuetest.net.Api;
import com.example.kanxuetest.net.HttpClientUtil;
import com.example.kanxuetest.utils.FileUtil;

import android.app.Application;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

public class MyApplication extends Application {

	private int m_versionCode;
	private ProgressDialog mProgressDialog;
	private boolean m_bCancel = false;
	private String m_appSavePath;

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
					final JSONObject jsonObject = new JSONObject(jsonData);
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
							final String versionName = versionCodeToName(jsonObject
									.optInt("version"));
							String size = converToSuitableSize(jsonObject
									.optInt("size"));
							builder.setMessage("版本" + versionName + "\n大小:"
									+ size + "\n是否更新？");
							builder.setPositiveButton("确定",
									new OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											mProgressDialog = createProgressDialog(context);
											asyncUpdateThread(versionName,
													jsonObject);
											mProgressDialog.show();
										}
									});
							builder.setCancelable(false);
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

	/**
	 * 应用更新的线程
	 */
	private void asyncUpdateThread(final String versionName,
			final JSONObject jsonObject) {
		new Thread() {
			public void run() {
				URL url = null;
				HttpURLConnection connection = null;
				String appSaveName = "kanxue" + versionName + ".apk";
				FileOutputStream fos = null;
				try {
					url = new URL(jsonObject.optString("url"));
					connection = (HttpURLConnection) url.openConnection();
					int fileLength = connection.getContentLength();
					if (FileUtil.isHaveSDK()) {
						String path = "kanxue/tempApk";
						File resultFile = FileUtil.getExternalFile(path,appSaveName);
						m_appSavePath = resultFile.getAbsolutePath();
						if (resultFile != null) {
							fos = new FileOutputStream(resultFile);
						}
					} else {
						fos = openFileOutput(appSaveName, MODE_PRIVATE);
						m_appSavePath = MyApplication.this.getFilesDir() + "/"
								+ appSaveName;
					}

					if (fileLength > 0) {
						mProgressDialog.setMax(fileLength);
						InputStream is = connection.getInputStream();
						// 读取大文件
						byte[] buffer = new byte[4 * 1024];
						int length = is.read(buffer);
						int downLength = 0;
						while (length != -1) {
							fos.write(buffer, 0, length);
							downLength += length;
							length = is.read(buffer);
							if (m_bCancel) {
								mHandler.sendEmptyMessage(-1);
								break;
							} else {
								mHandler.sendEmptyMessage(downLength);
							}
						}
						fos.flush();

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (fos != null) {
							fos.close();
						}
						if (connection != null) {
							connection.disconnect();
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			};
		}.start();
	}

	/**
	 * 创建一个进度对话框
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private ProgressDialog createProgressDialog(Context context) {
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setTitle("提示");
		mProgressDialog.setMessage("正在更新。。。。");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				m_bCancel = true;// 结束下载线程
			}
		});
		return mProgressDialog;
	}

	/**
	 * 版本号转换为名字
	 * 
	 * @param code
	 *            版本号
	 * @return
	 */
	private String versionCodeToName(int code) {
		return code / 100 + "." + code / 10 % 10 + "." + code % 100;
	}

	private String converToSuitableSize(int size) {
		if (size >= 1024) {
			return (size / 1024.0 + "").substring(0, 3) + "MB";
		}
		return size + "KB";
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			// 关闭更新对话框
			if (mProgressDialog == null) {
				return;
			}

			if (m_appSavePath == null) {
				return;
			}

			if (msg.what < 0) {
				return;
			}

			if (msg.what > 0 && msg.what != mProgressDialog.getMax()) {
				mProgressDialog.setProgress(msg.what);
				return;
			}
			mProgressDialog.dismiss();
			installApk(m_appSavePath);
		}
	};

	private void installApk(String m_appSavePath) {
		Uri uri=Uri.fromFile(new File(m_appSavePath));
		Intent intent=new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		startActivity(intent);
	};
}
