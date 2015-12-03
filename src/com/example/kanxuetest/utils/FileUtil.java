package com.example.kanxuetest.utils;

import java.io.File;

import android.annotation.SuppressLint;
import android.os.Environment;

@SuppressLint("NewApi")
public class FileUtil {

	/**
	 * �ж��Ƿ����SDK
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
	 * �õ��ⲿ�洢�ļ���·��
	 * 
	 * @param path
	 *            SDK�µ��ļ�·�� xxx/xxx
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
