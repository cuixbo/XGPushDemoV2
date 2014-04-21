package com.cui.activity;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cui.test.R;
import com.cui.util.DBUtil;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

public class LoginActivity extends Activity {
	EditText etUsername;
	Button btnLogin;
	TextView tvRegist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		etUsername = (EditText) findViewById(R.id.etUserame);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		tvRegist = (TextView) findViewById(R.id.tvRegist);
		createFiles();
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ("".equals(etUsername.getText().toString())) {
					return;
				}
				new Thread(new Runnable() {

					@Override
					public void run() {

						boolean ret = HttpUtils.requestLogin(etUsername.getText().toString());
						if (ret) {
							XGPushConfig.enableDebug(LoginActivity.this, true);
							// XGPushManager.re
							XGPushManager.registerPush(LoginActivity.this, etUsername.getText().toString(), new XGIOperateCallback() {
								@Override
								public void onSuccess(Object data, int flag) {
									System.out.println("registerPush success-->" + data);
								}

								@Override
								public void onFail(Object data, int errCode, String msg) {
									System.out.println("registerPush fail-->" + data);
								}
							});
							MyApplication.username = etUsername.getText().toString();
							startActivity(new Intent(LoginActivity.this, FriendsActivity.class));
						} else {

						}
						// TODO Auto-generated method stub

					}
				}).start();
			}
		});
		tvRegist.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), RegistActivity.class));
			}
		});
	}

	@Override
	protected void onStart() {
		super.onStart();
		etUsername.setText(MyApplication.username);
		XGPushManager.onActivityStarted(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		XGPushManager.onActivityStoped(this);
	}
	
	public void createFiles(){
		String sdcard=Environment.getExternalStorageDirectory().getAbsolutePath();
		String appSDPath=sdcard+"XGPushDemoV2";
		File rootDir=new File(appSDPath);
		if(!rootDir.exists()){
			rootDir.mkdirs();
		}
	}
}
