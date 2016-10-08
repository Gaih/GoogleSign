package com.yilegame.googlesdk.util;

import android.app.Activity;
import android.text.TextUtils;

import com.yilegame.sdk.utils.LogUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpUtils {
  private static final String TAG="gaolingshi";
	public static String doPost(String body, String url, final  Purchase purchase, final Activity activity) {
		String returnData1=null;
		if (TextUtils.isEmpty(url)) {
			LogUtil.e("HttpUtil", "HttpUtil doPost url is null ");
		} else {
			LogUtil.i("HttpUtil", "HttpUtil doPost url: " + url);
			if (TextUtils.isEmpty(body)) {
				LogUtil.e("HttpUtil", "HttpUtil doPost body is null ");
			} else {
				HttpURLConnection mHttpURLConnection = null;
				OutputStream localOutputStream = null;
				try {
					LogUtil.i(TAG,"支付平台请求之前");
					mHttpURLConnection = (HttpURLConnection) (new URL(url)).openConnection();
					mHttpURLConnection.setRequestMethod("POST");
					mHttpURLConnection.setRequestProperty("connection", "close");
					mHttpURLConnection.setRequestProperty("Content-Type", "text/html");
					mHttpURLConnection.setConnectTimeout(5000);
					mHttpURLConnection.setDoOutput(true);
					localOutputStream = mHttpURLConnection.getOutputStream();
					localOutputStream.write(body.getBytes("UTF-8"));
					localOutputStream.flush();
					LogUtil.i(TAG, "HttpUtil doPost mHttpURLConnection.getResponseCode():" + mHttpURLConnection.getResponseCode());
					final int e1 = mHttpURLConnection.getResponseCode();
					if (200 == e1) {
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								PurchaseDao.getInstatncePurchase(activity).delectPurchase(purchase);
							}
						});
						returnData1 = StreamTools.readFromStream(mHttpURLConnection
								.getInputStream());
						LogUtil.i(TAG, "HttpUtil doPost returnData:" + returnData1);
						return returnData1;
					}

				} catch (ConnectException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (ProtocolException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					mHttpURLConnection.disconnect();
				}
			}
		}
		return returnData1;
	}
}

