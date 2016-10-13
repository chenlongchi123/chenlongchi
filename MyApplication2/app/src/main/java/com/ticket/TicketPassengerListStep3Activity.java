package com.ticket;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.bean.Passenger;
import com.example.chen.myapplication.R;
import com.examplechen.my.MyaccountActivity;
import com.examplechen.my.MycontactEditActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.utils.CONSTANT;
import com.utils.NetUtils;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TicketPassengerListStep3Activity extends Activity {

	ListView lvTicketPassengerListStep3;
	List<Map<String, Object>> data;
	ArrayList<Boolean> ckFlg = new ArrayList<Boolean>();
	ProgressDialog pDialog = null;
	TicketPassengerListStep3 adapter = null;
	Button btnTicketPassengerListStep3;
	Passenger[] passengers = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticket_passenger_list_step3);

		lvTicketPassengerListStep3 = (ListView) findViewById(R.id.lvTicketPassengerListStep3);
		btnTicketPassengerListStep3 = (Button)findViewById(R.id.btnTicketPassengerListStep3);

		data = new ArrayList<Map<String, Object>>();

		adapter = new TicketPassengerListStep3();
		lvTicketPassengerListStep3.setAdapter(adapter);

		lvTicketPassengerListStep3
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
						// TODO Auto-generated method stub

						if (arg2 == 0) {
							Intent intent = new Intent();
							intent.setClass(
									TicketPassengerListStep3Activity.this,
									MyaccountActivity.class);
							startActivity(intent);
						} else {

							Intent intent = new Intent();
							intent.setClass(
									TicketPassengerListStep3Activity.this,
									MycontactEditActivity.class);
							intent.putExtra("row",
									(Serializable) data.get(arg2));
							startActivity(intent);
						}
					}
				});

		btnTicketPassengerListStep3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 传回数据
				Intent intent = new Intent();
				ArrayList<Passenger> returnData = new ArrayList<Passenger>();

				for (int i = 0; i < ckFlg.size(); i++) {
					boolean b = ckFlg.get(i); // unbox
					if (b) {
						returnData.add(passengers[i]);
					}
				}

				intent.putExtra("passengers", returnData);
				setResult(200, intent);

				finish();
			}
		});
	}

	class TicketPassengerListStep3 extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView,
							ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();

				convertView = LayoutInflater.from(
						TicketPassengerListStep3Activity.this).inflate(
						R.layout.item_ticket_passenger_list_step3, null);

				holder.tvTicketPassengerListStep3Name = (TextView) convertView
						.findViewById(R.id.tvTicketPassengerListStep3Name);
				holder.tvTicketPassengerListStep3IdCard = (TextView) convertView
						.findViewById(R.id.tvTicketPassengerListStep3IdCard);
				holder.tvTicketPassengerListStep3Tel = (TextView) convertView
						.findViewById(R.id.tvTicketPassengerListStep3Tel);
				holder.ckTicketPassengerListStep3 = (CheckBox) convertView
						.findViewById(R.id.ckTicketPassengerListStep3);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (data.size() > 0) {
				holder.tvTicketPassengerListStep3Name.setText(data
						.get(position).get("name").toString());
				holder.tvTicketPassengerListStep3IdCard.setText(data
						.get(position).get("idCard").toString());
				holder.tvTicketPassengerListStep3Tel.setText(data.get(position)
						.get("tel").toString());
				holder.ckTicketPassengerListStep3.setChecked(ckFlg
						.get(position));

				holder.ckTicketPassengerListStep3
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								ckFlg.set(position, !ckFlg.get(position));
								Toast.makeText(
										TicketPassengerListStep3Activity.this,
										ckFlg.toString(), Toast.LENGTH_SHORT)
										.show();
							}
						});
			}

			return convertView;
		}

	}

	class ViewHolder {
		TextView tvTicketPassengerListStep3Name;
		TextView tvTicketPassengerListStep3IdCard;
		TextView tvTicketPassengerListStep3Tel;
		CheckBox ckTicketPassengerListStep3;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (!NetUtils.check(TicketPassengerListStep3Activity.this)) {
			Toast.makeText(TicketPassengerListStep3Activity.this,
					getString(R.string.network_check), Toast.LENGTH_SHORT)
					.show();
			return; // 后续代码不执行
		}

		new PassengerListTask().execute();
	}

	class PassengerListTask extends AsyncTask<String, String, Object> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = ProgressDialog.show(
					TicketPassengerListStep3Activity.this, null, "正在加载中...",
					false, true);
		}

		@Override
		protected Object doInBackground(String... p) {
			// TODO Auto-generated method stub
			HttpPost post = new HttpPost(CONSTANT.HOST
					+ "/otn/TicketPassengerList");

			// 发送请求
			DefaultHttpClient client = new DefaultHttpClient();
			String result = "";

			try {
				// jsessionid
				SharedPreferences pref = getSharedPreferences("user",
						Context.MODE_PRIVATE);
				String value = pref.getString("Cookie", "");
				// 1
				BasicHeader header = new BasicHeader("Cookie", value);
				post.setHeader(header);

				// 超时设置
				client.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT,
						CONSTANT.REQUEST_TIMEOUT);
				client.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, CONSTANT.SO_TIMEOUT);

				HttpResponse response = client.execute(post);

				// 处理结果
				if (response.getStatusLine().getStatusCode() == 200) {

					String json = EntityUtils.toString(response.getEntity());
					Gson gson = new GsonBuilder().create();
					passengers = gson.fromJson(json,
							Passenger[].class);

					return passengers;

				} else {
					result = "2";
				}

				client.getConnectionManager().shutdown();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = "2";
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = "2";
			} catch (IOException e) {
				e.printStackTrace();
				result = "2";
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				result = "2";
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
				result = "3"; // 重新登录
			}

			return result;
		}

		@Override
		protected void onPostExecute(Object result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			data.clear();

			if (pDialog != null) {
				pDialog.dismiss();
			}

			if (result instanceof Passenger[]) {
				Passenger[] passengers = (Passenger[]) result;

				// 初始化ckFlg
				ckFlg.clear();
				for (int i = 0; i < passengers.length; i++) {
					ckFlg.add(false);
				}

				if (passengers.length == 0) {
					Toast.makeText(TicketPassengerListStep3Activity.this,
							"没有乘车人", Toast.LENGTH_SHORT).show();
				} else {
					for (Passenger passenger : passengers) {
						Map<String, Object> row1 = new HashMap<String, Object>();
						row1.put("name",
								passenger.getName() + "(" + passenger.getType()
										+ ")");
						row1.put("idCard", passenger.getIdType() + ":"
								+ passenger.getId());
						row1.put("tel", "电话:" + passenger.getTel());
						data.add(row1);
					}
				}

			} else if (result instanceof String) {
				if ("3".equals(result.toString())) {
					Toast.makeText(TicketPassengerListStep3Activity.this,
							"请重新登录", Toast.LENGTH_SHORT).show();

				} else {
					Toast.makeText(TicketPassengerListStep3Activity.this,
							"服务器错误，请重试", Toast.LENGTH_SHORT).show();
				}
			}

			adapter.notifyDataSetChanged();

		}

	}



}
