package com.example.kanxuetest.utils;

import java.io.File;

import android.annotation.SuppressLint;
import android.os.Environment;

@SuppressLint("NewApi")
public class FileUtil {

	/**
	 * 判断是否存在SDK
	 * 
	 * @return
	 */
	public static boolean isHaveSDK() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 得到外部存储文件的路径
	 * 
	 * @param path
	 *            SDK下的文件路径 xxx/xxx
	 * @return
	 */
	public static File getExternalFile(String path, String name) {
		try {
			String wholePath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/" + path;
			File file = new File(wholePath);
			if (!file.exists()) {
				file.mkdirs();
			}
			
			String filePath = file.getAbsolutePath() + "/" + name;
			file = new File(filePath);

			if (file.exists()) {
				file.delete();
			}

			file.createNewFile();

			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
