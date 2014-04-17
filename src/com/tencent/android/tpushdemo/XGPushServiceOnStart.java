package com.tencent.android.tpushdemo;

import android.content.Context;
import android.content.Intent;

import com.cui.test.R;
import com.tencent.android.tpush.XGIPushServiceOnStart;
//import com.tencent.feedback.eup.CrashReport;

public class XGPushServiceOnStart implements XGIPushServiceOnStart {

	@Override
	public void onStart(Context context, Intent intent) {
//		CrashReport.initCrashReport(context, false);
	}
}
