package com.tencent.android.tpushdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cui.test.R;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.logging.LogUtil;

public class ViewLogActivity extends Activity {

	private static class HandlerExtension extends Handler {
		WeakReference<ViewLogActivity> mActivity;

		HandlerExtension(ViewLogActivity activity) {
			mActivity = new WeakReference<ViewLogActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ViewLogActivity theActivity = mActivity.get();
			TextView logView = (TextView) theActivity.findViewById(R.id.textview_log);
			ScrollView sv = (ScrollView) theActivity.findViewById(R.id.scrollView1);
			Message m = null;

			switch (msg.what) {
			case 1:
				if (logView != null) {
					String log = getLog();
					if (log != null) {
						logView.setText(log);
					}
				}

				m = this.obtainMessage(2);
				this.sendMessageDelayed(m, 200L);
				break;
			case 2:

				if (sv != null) {
					sv.fullScroll(ScrollView.FOCUS_DOWN);
				}

				break;
			}
		}

		private String getLog() {
			File[] files = LogUtil.getlogFiles(System.currentTimeMillis(), System.currentTimeMillis());
			if (files != null && files.length > 0) {
				File log = files[0];

				FileReader fr = null;
				LineNumberReader lnr = null;
				try {
					fr = new FileReader(log);
					lnr = new LineNumberReader(fr);
					String line = "";
					StringBuilder logBuilder = new StringBuilder();
					while ((line = lnr.readLine()) != null) {
						// System.out.println(line);
						logBuilder.append(line);
						logBuilder.append("\n\r");
					}

					if (fr != null) {
						fr.close();
					}

					if (lnr != null) {
						lnr.close();
					}

					if (logBuilder.length() > 0) {
						return logBuilder.toString();
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "Log load fial." + e.getMessage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "Log load fial." + e.getMessage();
				}
			}

			return null;
		}
	}

	private TextView logView = null;

	private ScrollView sv = null;

	private HandlerThread thread = new HandlerThread("viewLog");

	private Handler handler = null;

	private Message m = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_viewlog);

		logView = (TextView) findViewById(R.id.textview_log);
		sv = (ScrollView) findViewById(R.id.scrollView1);

		thread.start();

		handler = new HandlerExtension(this);

		m = handler.obtainMessage(1);
		handler.sendMessageDelayed(m, 100);

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (logView != null) {
			String log = getLog();
			if (log != null) {
				logView.setText(log);
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

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

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (thread != null) {
			thread.quit();
		}

		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
			handler = null;
		}
	}

	private String getLog() {
		File[] files = LogUtil.getlogFiles(System.currentTimeMillis(), System.currentTimeMillis());
		if (files != null && files.length > 0) {
			File log = files[0];

			FileReader fr = null;
			LineNumberReader lnr = null;
			try {
				fr = new FileReader(log);
				lnr = new LineNumberReader(fr);
				String line = "";
				StringBuilder logBuilder = new StringBuilder();
				while ((line = lnr.readLine()) != null) {
					// System.out.println(line);
					logBuilder.append(line);
					logBuilder.append("\n\r");
				}

				if (fr != null) {
					fr.close();
				}

				if (lnr != null) {
					lnr.close();
				}

				if (logBuilder.length() > 0) {
					return logBuilder.toString();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Log load fial." + e.getMessage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "Log load fial." + e.getMessage();
			}
		}

		return null;
	}

}
