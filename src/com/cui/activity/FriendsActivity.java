package com.cui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cui.test.R;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;
import com.tencent.android.tpushdemo.CustomPushReceiver;

public class FriendsActivity extends Activity {

	ListView lvFriends;
	ArrayList<String> listData = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends);
		lvFriends = (ListView) findViewById(R.id.lvFriends);
		new Thread(new Runnable() {

			@Override
			public void run() {
				listData = HttpUtils.requestFriends();
				FriendsActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						lvFriends.setAdapter(new ArrayAdapter<String>(FriendsActivity.this, android.R.layout.simple_expandable_list_item_1, listData));
					}
				});
			}
		}).start();
		lvFriends.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FriendsActivity.this, ConversationActivity.class);
				intent.putExtra("VALUE", listData.get(position));
				startActivity(intent);
			}
		});

	}

}
