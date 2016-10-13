package com.ticket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.chen.myapplication.MainActivity;
import com.example.chen.myapplication.R;
import com.karics.library.zxing.android.CaptureActivity;
import com.utils.ZxingUtils;


public class TicketPaySuccessStep5Activity extends Activity {

	Button btnTicketPaySuccessStep5;
	ImageButton ivTicketPaySuccessStep5;
	private static final int REQUEST_CODE_SCAN = 0x0000;
	private static final String DECODED_CONTENT_KEY = "codedContent";
	private static final String DECODED_BITMAP_KEY = "codedBitmap";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_pay_success_step5);

		btnTicketPaySuccessStep5 = (Button) findViewById(R.id.btnTicketPaySuccessStep5);
		ivTicketPaySuccessStep5 = (ImageButton) findViewById(R.id.ivTicketPaySuccessStep5);
		//将资源文件转换成Bitmap
		Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.miao);
		ivTicketPaySuccessStep5.setImageBitmap(ZxingUtils.addLogo(ZxingUtils.createQRImage("我的订单,没错，这就是我的订单，哈哈哈", 160, 160),bitmap));
		// 创建二维码
		ivTicketPaySuccessStep5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TicketPaySuccessStep5Activity.this, CaptureActivity.class);
				startActivityForResult(intent, REQUEST_CODE_SCAN);

			}
		});


		btnTicketPaySuccessStep5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(TicketPaySuccessStep5Activity.this,
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 扫描二维码/条码回传
		if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
			if (data != null) {

				String content = data.getStringExtra(DECODED_CONTENT_KEY);
				Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);

			}
		}
	}


}
