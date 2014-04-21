package com.cui.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cui.test.R;
import com.cui.util.DBUtil;
import com.tencent.android.tpush.XGPushTextMessage;
import com.tencent.android.tpushdemo.CustomPushReceiver;
/**
 * messagetype 短消息类型,收1,发2,默认0
 * readed 是否已读,已读1,未读2,默认0
 * status 发送状态,正在发送1,发送成功2,发送失败3,默认0
 * @author xiaobocui
 *
 */
public class ConversationActivity extends Activity {
	//TextView tvConversation;
	EditText etMessage;
	Button btnSend;
	ListView lvConversation;
	String toId;
	ArrayList<XGMessage> list=new ArrayList<XGMessage>();
	ArrayAdapter<XGMessage> adapter;
	private SoundPool soundPool;
	private int soundid_s1,soundid_s2,soundid_s3,soundid_s4;
	private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private MyCursorAdapter mAdapter;
	private MyPushReceiver receiver;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation);
		toId = getIntent().getStringExtra("VALUE");
		//loadConversations();
		//adapter=new ArrayAdapter<XGMessage>(getApplicationContext(), android.R.id.text1, list);
		DBUtil dbUtil = DBUtil.getInstance(getApplicationContext());
		Cursor cursor=dbUtil.query("select * from conversations where ID=?", new String[]{toId});
		lvConversation = (ListView) findViewById(R.id.lvConversation);
		mAdapter=new MyCursorAdapter(getApplicationContext(), cursor, true);
		lvConversation.setAdapter(mAdapter);
		//tvConversation = (TextView) findViewById(R.id.tvConversation);
		etMessage = (EditText) findViewById(R.id.etMessage);
		btnSend = (Button) findViewById(R.id.btnSend);
		receiver=new MyPushReceiver();
		registerReceiver(receiver, new IntentFilter("com.tencent.android.tpush.action.PUSH_MESSAGE"));
		initSounds();
		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ("".equals(etMessage.getText().toString())) {
					return;
				}
				new Thread(new Runnable() {

					@Override
					public void run() {
						String content=etMessage.getText().toString();
						HttpUtils.sendMessage(MyApplication.username + ";" + toId + ";" + content);
						DBUtil dbUtil = DBUtil.getInstance(getApplicationContext());
						dbUtil.insert("insert into conversations (ID,fromid,toid,content,messagetime,messagetype,readed,status) values(?,?,?,?,?,?,?,?)",
								new String[] {toId,MyApplication.username,toId,content,sdf.format(new Date()),"2","0","0"});
						
						ConversationActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								//loadConversations();
								DBUtil dbUtil = DBUtil.getInstance(getApplicationContext());
								Cursor  cursor=dbUtil.query("select * from conversations where ID=?", new String[]{toId});
								mAdapter.changeCursor(cursor);
								etMessage.setText("");
							}
						});
					}
				}).start();

			}
		});
	}

	private void loadConversations() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				DBUtil dbUtil = DBUtil.getInstance(getApplicationContext());
				Cursor cursor=dbUtil.query("select * from conversations where toid=?", new String[]{toId});
				list=new ArrayList<XGMessage>();
				if(cursor.getCount()>0){
					while(cursor.moveToNext()){
						XGMessage message=new XGMessage();
						message.from=cursor.getString(cursor.getColumnIndex("fromid"));
						message.to=cursor.getString(cursor.getColumnIndex("toid"));
						message.sendtime=cursor.getString(cursor.getColumnIndex("sendtime"));
						message.content=cursor.getString(cursor.getColumnIndex("content"));
						list.add(message);
					}
					cursor.close();
				}
				adapter.notifyDataSetChanged();
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	
	
	private void initSounds() {
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundid_s1=soundPool.load(getApplicationContext(),R.raw.s1, 1);
		soundid_s2=soundPool.load(getApplicationContext(),R.raw.s2, 1);
		soundid_s3=soundPool.load(getApplicationContext(),R.raw.s3, 1);
		soundid_s4=soundPool.load(getApplicationContext(),R.raw.s4, 1);
	}

	public void playSound(int soundid) {
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
		soundPool.play(soundid, streamVolume, streamVolume, 1,0, 1f);
	}
	
	class XGMessage{
		public String from;
		public String to;
		public String sendtime;
		public String content;
	}
	
	class MyCursorAdapter extends CursorAdapter{

		public MyCursorAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			TextView view=new TextView(context);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			TextView textView=(TextView)view;
			textView.setTextSize(16f);
			textView.setTextColor(Color.BLACK);
			textView.setText(cursor.getString(cursor.getColumnIndex("FROMID"))+":"+cursor.getString(cursor.getColumnIndex("CONTENT")));
		}
		
	}
	class MyPushReceiver extends CustomPushReceiver{
		@Override
		public void onTextMessage(Context context, XGPushTextMessage message) {
			new Handler(getMainLooper()).postDelayed(new Runnable() {
				
				@Override
				public void run() {
					DBUtil dbUtil = DBUtil.getInstance(getApplicationContext());
					Cursor  cursor=dbUtil.query("select * from conversations where ID=?", new String[]{toId});
					mAdapter.changeCursor(cursor);
				}
			}, 300);
			//super.onTextMessage(context, message);
		}
	}
}
