package com.feixun.phiaccount.util;

import android.os.AsyncTask;
import android.util.Log;

//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.util.ByteArrayBuffer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;


public class AccountAyncTask extends AsyncTask<Object, Object, byte[]> {
	private IHttpCallBack requestCallBack;
	private String url;
	private Map<String,String> params;
	private int httpstatus;
	private String httpRes;

	public AccountAyncTask(String url, Map<String,String> params,
						   IHttpCallBack callback) {
		this.requestCallBack = callback;
		this.params = params;
		this.url = url;
		Log.i("AccountAyncTask", "uri = " + url);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (this.requestCallBack != null) {
			requestCallBack.onStart();
		}
	}

	@Override
	protected byte[] doInBackground(Object... arg0) {
		BufferedInputStream bis = null;
		byte[] result = null;
		String encode = "UTF-8";

		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
		String paramsStr = gson.toJson(params);

		Log.i("params ",paramsStr);
		try{
			URL urlobj = new URL(this.url);
			HttpURLConnection conn = (HttpURLConnection)urlobj.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-type", "application/x-javascript; charset=" + encode);
			conn.setConnectTimeout(5 * 1000);
			OutputStream out = conn.getOutputStream();
			byte[] datas = paramsStr.getBytes();
			out.write(datas);
			out.flush();
			out.close();
			Log.i("result ","code ----" + conn.getResponseCode());
			if(conn.getResponseCode() == 200){
				String jsonResult="";
				byte[] data=new byte[1024];
				int len=0;
				InputStream in=conn.getInputStream();
				if((len=in.read(data))!=-1){
					jsonResult +=new String(data);
				}
				Log.i("k-123 jsonresult=",jsonResult);
				result = jsonResult.getBytes();
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private void closeQuietly(InputStream bis) {
		if (bis != null) {
			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
				if (this.requestCallBack != null) {
					requestCallBack.onFailure(e.getMessage());
				}
			}
		}
	}

	@Override
	protected void onPostExecute(byte[] data) {
		if (this.requestCallBack != null && data != null && data.length > 0) {
			Log.i("k-123", "not null" + data);
			requestCallBack.onSuccess(data);
		}
		if (this.requestCallBack != null && data == null) {
			Log.i("k-123", "null" + data);
			requestCallBack.onFailure("Http error, " + httpRes);
		}
		super.onPostExecute(data);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (this.requestCallBack != null) {
			requestCallBack.onCancel();
			this.requestCallBack = null;
		}
	}
}
