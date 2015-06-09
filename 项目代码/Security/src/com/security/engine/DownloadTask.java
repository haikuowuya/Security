package com.security.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;

public class DownloadTask
{

	public static File getFile(String path, String filePath,
			ProgressDialog progressDialog) throws Exception
	{
		URL url = new URL(path);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setConnectTimeout(2000);
		httpURLConnection.setRequestMethod("GET");
		if (httpURLConnection.getResponseCode() == 200)
		{
			int total = httpURLConnection.getContentLength();
			progressDialog.setMax(total);

			InputStream is = httpURLConnection.getInputStream();
			File file = new File(filePath);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len;
			int process = 0;
			while ((len = is.read(buffer)) != -1)
			{
				fos.write(buffer, 0, len);
				process += len;
				progressDialog.setProgress(process);
			}
			fos.flush();
			fos.close();
			is.close();
			return file;
		}
		return null;
	}

}
