package com.tencent.android.tpushdemo;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cui.test.R;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.common.Constants;
import com.tencent.android.tpush.horse.Tools;
import com.tencent.android.tpush.logging.TLog;
import com.tencent.android.tpush.service.cache.CacheManager;

//import com.tencent.feedback.eup.CrashReport;

public class MainActivity extends Activity {
	@Override
	protected void onStart() {
		super.onStart();
		XGPushManager.onActivityStarted(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		XGPushManager.onActivityStoped(this);
	}

	private RadioGroup radioGroup;
	private TextView tv_server;
	private Context mContext;
	// String accessKey = "354_secret_key";
	// long accessId = 354;
	// String accessKey = "key1";
	// long accessId = 100L;
	private static String curServerInfo = "";
	private boolean flag = false;
	// String acc = "303916748";
	// String tick = "@bMOnIJjen";

	Message m = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TLog.init(getApplicationContext());
		setContentView(R.layout.activity_main);
		XGPushConfig.enableDebug(this, true);
		XGPushManager.registerPush(getApplicationContext());
		mContext = this;
		radioGroup = (RadioGroup) findViewById(R.id.rg_type);
		setTitle("XGPushDemoV2");
		// 初始化RDQ，crash上报的组件
		// initCrashLogRDQ();

		// XGPushConfig.setAccessId(this, accessId);
		// XGPushConfig.setAccessKey(this, accessKey);

		tv_server = (TextView) findViewById(R.id.tv_server);
		if (savedInstanceState != null && savedInstanceState.containsKey("strategy")) {
			// tv_server.setText(savedInstanceState.getString("strategy"));
			curServerInfo = savedInstanceState.getString("strategy");
			JSONObject json = new JSONObject();
		}
		mContext.registerReceiver(receiver, new IntentFilter("com.tencent.android.tpush.horse"));
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_http:
					Tools.setChannelType(mContext, 2);
					break;
				case R.id.rb_tcp:
					Tools.setChannelType(mContext, 1);
					break;
				case R.id.rb_http_wap:
					Tools.setChannelType(mContext, 3);
					break;
				case R.id.rb_other:
				default:
					Tools.setChannelType(mContext, 0);
					break;
				}
				if (!flag) {
					return;
				}
				Tools.clearCacheServerItems(mContext);
				Tools.clearOptStrategyItem(mContext);
				Tools.saveCurStrategyItem(mContext, null);
				ActivityManager actMgr = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningAppProcessInfo> appList2 = actMgr.getRunningAppProcesses();
				for (RunningAppProcessInfo info : appList2) {
					if (info.processName.contains(":tpush_service")) {
						android.os.Process.killProcess(info.pid);
						tv_server.setText("");
						Toast.makeText(mContext, "已结束当前进程，等待重启", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
		// 启动service
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				XGPushManager.startPushService(mContext);
				XGPushConfig.enableDebug(mContext, true);
			}
		});

		// 注册push
		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(Constants.LogTag, "$$$ register push");
				XGPushConfig.enableDebug(MainActivity.this, true);
				//XGPushManager.re
				XGPushManager.registerPush(MainActivity.this, "cui",new XGIOperateCallback() {
					@Override
					public void onSuccess(Object data, int flag) {
						System.out.println("registerPush success-->"+data);
						Log.w(Constants.LogTag, "+++ register push sucess. token:" + data);
						m = handler.obtainMessage();
						m.obj = "+++ register push sucess. token:" + data;
						m.sendToTarget();
					}

					@Override
					public void onFail(Object data, int errCode, String msg) {
						Log.w(Constants.LogTag, "+++ register push fail. token:" + data + ", errCode:" + errCode + ",msg:" + msg);

						m = handler.obtainMessage();
						m.obj = "+++ register push fail. token:" + data + ", errCode:" + errCode + ",msg:" + msg;
						m.sendToTarget();
					}
				});

			}

		});

		// 反注册push
		findViewById(R.id.button3).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(Constants.LogTag, "$$$ unregister push");

				try {
					XGPushManager.unregisterPush(MainActivity.this, XGPushConfig.getToken(getApplication()), new XGIOperateCallback() {

						@Override
						public void onSuccess(Object data, int flag) {
							Log.w(Constants.LogTag, "+++ unregisterPush push sucess. flag:" + flag);

							m = handler.obtainMessage();
							m.obj = "+++ unregisterPush push sucess. flag:" + flag;
							m.sendToTarget();
						}

						@Override
						public void onFail(Object data, int errCode, String msg) {
							Log.w(Constants.LogTag, "+++ unregisterPush push fail. token:" + data + ", errCode:" + errCode + ",msg:" + msg);

							m = handler.obtainMessage();
							m.obj = "+++ unregisterPush push fail. token:" + data + ", errCode:" + errCode + ",msg:" + msg;
							m.sendToTarget();
						}

					});
				} catch (Exception e) {
					e.printStackTrace();

					Toast.makeText(getApplicationContext(), "unregister fail! " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}

		});

		// 心跳
		findViewById(R.id.button4).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(Constants.LogTag, "$$$ heartbeat");
				Context context = getApplicationContext();
				if (context != null) {
					context.sendBroadcast(new Intent("com.tencent.android.tpush.service.channel.heartbeatIntent"));
				}
			}
		});

		// 设置标签
		findViewById(R.id.btn_setTag).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(Constants.LogTag, "$$$ set tag");
				Context ctx = MainActivity.this;
				if (ctx != null) {
					LinearLayout layout = new LinearLayout(ctx);
					layout.setOrientation(LinearLayout.VERTICAL);
					final EditText textviewGid = new EditText(ctx);
					textviewGid.setHint("请输入标签名称");
					layout.addView(textviewGid);

					AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
					builder.setView(layout);
					builder.setPositiveButton("设置标签", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							String text = textviewGid.getText().toString();
							if (text != null && text.length() != 0) {
								XGPushManager.setTag(MainActivity.this, text);
							}
						}
					});
					builder.show();
				}
			}
		});

		// 删除标签
		findViewById(R.id.btn_delTag).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(Constants.LogTag, "$$$ del tag");
				Context ctx = MainActivity.this;
				if (ctx != null) {
					LinearLayout layout = new LinearLayout(ctx);
					layout.setOrientation(LinearLayout.VERTICAL);
					final EditText textviewGid = new EditText(ctx);
					textviewGid.setHint("请输入标签名称");
					layout.addView(textviewGid);

					AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
					builder.setView(layout);
					builder.setPositiveButton("删除标签", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

							String text = textviewGid.getText().toString();
							if (text != null && text.length() != 0) {
								XGPushManager.deleteTag(MainActivity.this, text);
							}
						}
					});
					builder.show();
				}
			}
		});

		/*
		 * findViewById(R.id.button6).setOnClickListener(new OnClickListener(){
		 * 
		 * @Override public void onClick(View v) { Log.d(Constants.LogTag, "$$$ update msg id"); ArrayList<MessageId> list = new ArrayList<MessageId>(); for(int i = 0; i < 100; i++){ MessageId msg =
		 * new MessageId(); msg.id = 10000L + i; msg.isAck = MessageId.FLAG_UNACK; msg.receivedTime = System.currentTimeMillis(); list.add(msg); }
		 * MessageManager.getInstance().updateMsgId(getApplication(), getApplication().getPackageName(), list); } });
		 */

		/*
		 * findViewById(R.id.button7).setOnClickListener(new OnClickListener(){
		 * 
		 * @Override public void onClick(View v) { Log.d(Constants.LogTag, "$$$ get msg id"); ArrayList<MessageId> list = MessageManager.getInstance ().getMessageIdListByPkgName(getApplication(),
		 * getApplication().getPackageName()); if(list != null){ for(MessageId id : list){ Log.v(Constants.LogTag, ">>> msg id " + id.id + ", ack " + id.isAck + ", time " + id.receivedTime); } } } });
		 */

		findViewById(R.id.button8).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(Constants.LogTag, "$$$ request push");
				String str = "{\"title \":\"xxx\", \"content \":\"xxxxxxxxx\",  \"accept_time\": [{\"start\":{\"hour\":\"13\",\"min\":\"00\"}, \"end\": {\"hour\":\"14\",\"min\":\"00\"} },{\"start\":{\"hour\":\"00\",\"min\":\"00\"}, \"end\": {\"hour\":\"09\",\"min\":\"00\"} }],\"custom_content\":{\"key1\": \"value1\",\"key2\": \"value2\"}}";
				try {
					JSONObject contentJson = new JSONObject(str);
					JSONArray actTimesJson = new JSONArray(contentJson.getString("accept_time"));
					TLog.e("test", "" + actTimesJson.length());
					for (int i = 0; i < actTimesJson.length(); i++) {
						TLog.e("test", actTimesJson.getString(i));
						JSONObject accptJson = new JSONObject(actTimesJson.getString(i));
						TLog.e("test", accptJson.getString("start"));
						JSONObject startJson = new JSONObject(accptJson.getString("start"));
						TLog.e("test", startJson.getString("hour") + ":" + startJson.getString("min"));
						JSONObject endJson = new JSONObject(accptJson.getString("end"));
						TLog.e("test", endJson.getString("hour") + ":" + endJson.getString("min"));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// new requestPush().start();
			}
		});

		/*
		 * findViewById(R.id.button9).setOnClickListener(new OnClickListener(){
		 * 
		 * @Override public void onClick(View v) { Log.d(Constants.LogTag, "$$$ report test"); PushServiceManager.setContext(getApplicationContext());
		 * 
		 * ReportItem item = new ReportItem(100L, 0, 0, 200, 0, "127.0.0.1", 200, "detail"); ReportManager.getInstance().report(item);
		 * 
		 * } });
		 */

		/*
		 * findViewById(R.id.button10).setOnClickListener(new OnClickListener(){
		 * 
		 * @Override public void onClick(View v) { Log.d(Constants.LogTag, "$$$ intent encrypt");
		 * 
		 * PushServiceManager.setContext(getApplicationContext());
		 * 
		 * TpnsPushClientReq req = new TpnsPushClientReq(); req.msgList = new ArrayList<TpnsPushMsg>(); TpnsPushMsg msg = new TpnsPushMsg(); msg.accessId = 100L; msg.appPkgName = getPackageName();
		 * msg.msgId = 1000L;
		 * 
		 * NotifyMsg nMsg = new NotifyMsg(); nMsg.title = "title"; nMsg.content = "content";
		 * 
		 * msg.notifyMsg = nMsg;
		 * 
		 * req.msgList.add(msg);
		 * 
		 * MessageManager.getInstance().messageDistribute(req, new ExtraInfo());
		 * 
		 * } });
		 */

		// 查看日志
		findViewById(R.id.button11).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, ViewLogActivity.class);
				startActivity(intent);

			}
		});

		// kill Process
		findViewById(R.id.button12).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Tools.saveCurStrategyItem(mContext, null);
				ActivityManager actMgr = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningAppProcessInfo> appList2 = actMgr.getRunningAppProcesses();
				for (RunningAppProcessInfo info : appList2) {
					if (info.processName.contains(":tpush_service")) {
						android.os.Process.killProcess(info.pid);
						tv_server.setText("");
						Toast.makeText(mContext, "已结束当前进程，等待重启", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		// 清除cache
		findViewById(R.id.button13).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Tools.clearCacheServerItems(mContext);
				Tools.clearOptStrategyItem(mContext);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private static class HandlerExtension extends Handler {
		WeakReference<MainActivity> mActivity;

		HandlerExtension(MainActivity activity) {
			mActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			MainActivity theActivity = mActivity.get();
			if (msg != null) {
				Toast.makeText(theActivity.getApplication(), "" + msg.obj, Toast.LENGTH_LONG).show();
			}
		}
	}

	class requestPush extends Thread {

		@Override
		public void run() {
			super.run();

			Log.v(Constants.LogTag, "request push thread started @" + getId());

			HttpClient client = new DefaultHttpClient();

			String token = CacheManager.getToken(getApplication());
			Log.i(Constants.LogTag, "get token from cache  @" + token);
			if (token == null || token.length() == 0) {
				// Toast.makeText(getApplication(),
				// "request push fial @@ token is not found!",
				// Toast.LENGTH_SHORT).show();
				m = handler.obtainMessage();
				m.obj = "request push fial @@ token is not found!";
				m.sendToTarget();
				return;
			}

			String url = "http://183.61.46.193:80/index.php?token=" + token + "&accessid=" + XGPushConfig.getAccessId(mContext);

			Log.d(Constants.LogTag, "combi url  @" + url);

			HttpUriRequest getRequest = new HttpGet(url);
			try {
				Log.d(Constants.LogTag, "excete request");
				HttpResponse response = client.execute(getRequest);

				StatusLine status = response.getStatusLine();
				int statusCode = status.getStatusCode();
				Log.d(Constants.LogTag, "status code  @" + statusCode);

				if (statusCode == 200) {
					// Toast.makeText(getApplication(), "request push success!",
					// Toast.LENGTH_SHORT).show();
					m = handler.obtainMessage();
					m.obj = "request push success!";
					m.sendToTarget();
				} else {
					// Toast.makeText(getApplication(), "request push fail!",
					// Toast.LENGTH_SHORT).show();
					m = handler.obtainMessage();
					m.obj = "request push fail!";
					m.sendToTarget();
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
				// Toast.makeText(getApplication(), "request push fail!" +
				// e.getMessage(), Toast.LENGTH_LONG).show();
				m = handler.obtainMessage();
				m.obj = "request push fail!" + e.getMessage();
				m.sendToTarget();
			} catch (IOException e) {
				e.printStackTrace();
				// Toast.makeText(getApplication(), "request push fail!" +
				// e.getMessage(), Toast.LENGTH_LONG).show();
				m = handler.obtainMessage();
				m.obj = "request push fail!" + e.getMessage();
				m.sendToTarget();
			}
		}
	}

	private Handler handler = new HandlerExtension(MainActivity.this);

	protected void onResume() {
		super.onResume();

		tv_server.setText(Tools.getCurStrategyItem(mContext));
		int type = Tools.getChannelType(mContext);
		Log.i("Jie", "curinfo=" + curServerInfo + " type=" + type);
		switch (type) {
		case 0:
			radioGroup.check(R.id.rb_other);
			break;
		case 1:
			radioGroup.check(R.id.rb_tcp);
			break;
		case 2:
			radioGroup.check(R.id.rb_http);
			break;
		case 3:
			radioGroup.check(R.id.rb_http_wap);
			break;

		default:
			break;
		}
		flag = true;
	};

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				String action = intent.getAction();
				if ("com.tencent.android.tpush.horse".equals(action)) {
					curServerInfo = intent.getStringExtra("strategy") + "";
					Log.i("Jie", "recvInfo=" + curServerInfo);
					tv_server.setText(curServerInfo);
				}
			}
		};
	};

	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	};

	// private void initCrashLogRDQ() {
	// // 初始化Crash上报
	// CrashReport.initCrashReport(getApplicationContext(), true);
	//
	// // 初始化Native Crash上报
	// String tombDirectoryPath = this.getDir("tomb", Context.MODE_PRIVATE)
	// .getAbsolutePath();
	// CrashReport.initNativeCrashReport(getApplicationContext(),
	// tombDirectoryPath, true);
	// }
}
