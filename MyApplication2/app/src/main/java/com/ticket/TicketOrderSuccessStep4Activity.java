package com.ticket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.chen.myapplication.MainActivity;
import com.example.chen.myapplication.R;


public class TicketOrderSuccessStep4Activity extends Activity {

	TextView tvTicketOrderSuccessStep4Back;
	TextView tvTicketOrderSuccessStep4Pay;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_order_success_step4);

		tvTicketOrderSuccessStep4Back = (TextView) findViewById(R.id.tvTicketOrderSuccessStep4Back);
		tvTicketOrderSuccessStep4Pay = (TextView) findViewById(R.id.tvTicketOrderSuccessStep4Pay);

		tvTicketOrderSuccessStep4Back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(TicketOrderSuccessStep4Activity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		tvTicketOrderSuccessStep4Pay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 跳转到下一步
				Intent intent = new Intent();
				intent.setClass(TicketOrderSuccessStep4Activity.this, TicketPaySuccessStep5Activity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(TicketOrderSuccessStep4Activity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}

		return super.onKeyDown(keyCode, event);
	}



}
