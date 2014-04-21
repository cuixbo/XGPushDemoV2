package com.cui.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.cui.test.R;
import com.cui.util.DBUtil;

public class RegistActivity extends Activity {
	EditText etUsername;
	Button btnRegist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regist);
		etUsername = (EditText) findViewById(R.id.etUserame);
		btnRegist = (Button) findViewById(R.id.btnRegist);
		btnRegist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ("".equals(etUsername.getText().toString())) {
					return;
				}
				new Thread(new Runnable() {

					@Override
					public void run() {
						boolean ret = HttpUtils.registUsername(etUsername.getText().toString());
						if (ret) {
							DBUtil dbUtil=DBUtil.getInstance(getApplicationContext());
							dbUtil.insert("insert into userinfo (name) values(?)", new String[]{etUsername.getText().toString()});
							MyApplication.username=etUsername.getText().toString();
							RegistActivity.this.finish();
						} else {

						}

					}
				}).start();
			}
		});
	}

}

class HttpUtils {
	static String url = "http://192.168.0.109:8080/XGPushServer/QueryServer.do";

	public static boolean registUsername(String username) {
		try {
			HttpResponse httpResponse = null;
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("type", "101"));
			params.add(new BasicNameValuePair("username", username));
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			httpResponse = new DefaultHttpClient().execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(httpResponse.getEntity());
				if ("0".equals(result)) {
					return true;
				}
				return false;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean sendMessage(String message) {
		try {
			HttpResponse httpResponse = null;
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("type", "102"));
			params.add(new BasicNameValuePair("message", message));
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			httpResponse = new DefaultHttpClient().execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(httpResponse.getEntity());
				if ("0".equals(result)) {
					return true;
				}
				return false;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static ArrayList<String> requestFriends() {
		try {
			HttpResponse httpResponse = null;
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("type", "103"));
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			httpResponse = new DefaultHttpClient().execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				ArrayList<String> list = new ArrayList<String>();
				String result = EntityUtils.toString(httpResponse.getEntity());
				String results[] = result.split(";");
				for (int i = 0; i < results.length; i++) {
					list.add(results[i]);
				}
				return list;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean requestLogin(String username) {
		try {
			HttpResponse httpResponse = null;
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("type", "104"));
			params.add(new BasicNameValuePair("username", username));
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpPost.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
			httpPost.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			httpResponse = new DefaultHttpClient().execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(httpResponse.getEntity());
				if ("0".equals(result)) {
					return true;
				}
				return false;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
