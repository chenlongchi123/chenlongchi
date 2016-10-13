package com.examplechen.my;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.chen.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.utils.CONSTANT;
import com.utils.NetUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MycontactAddActivity extends AppCompatActivity {
    private  EditText add_contact_name=null;
    private Spinner Spinner_type=null;
    private EditText add_contact_num=null;
    private  Spinner Spinner_type_man=null;
    private EditText add_contact_tel=null;
    private  Button bt_add_contact=null;
    ProgressDialog pDialog = null;
    String action = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycontact_add);
        viewInit();
        android.support.v7.app.ActionBar bar=getSupportActionBar();
        bar.setTitle("添加联系人");
    }

     void viewInit() {
         add_contact_name=(EditText)findViewById(R.id.add_contact_name);
         Spinner_type=(Spinner)findViewById(R.id.Spinner_type);
         add_contact_num=(EditText)findViewById(R.id.add_contact_num);
         Spinner_type_man=(Spinner)findViewById(R.id.Spinner_type_man);
         add_contact_tel=(EditText)findViewById(R.id.add_contact_tel);
         bt_add_contact=(Button)findViewById(R.id.bt_add_contact);
         bt_add_contact.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (TextUtils.isEmpty(add_contact_name.getText().toString())) {
                     add_contact_name.setError("请输入姓名");
                     add_contact_name.requestFocus();//移动光标到EditText处
                 } else if (TextUtils.isEmpty(add_contact_num.getText().toString())) {
                     add_contact_num.setError("请输入身份证号");
                     add_contact_num.requestFocus();
                 } else if (TextUtils.isEmpty(add_contact_tel.getText().toString())) {
                 add_contact_tel.setError("请输入电话");
                 add_contact_tel.requestFocus();//移动光标到EditText处
                 } else {
                     if (!NetUtils.check(MycontactAddActivity.this)) {
                         Toast.makeText(MycontactAddActivity.this,
                                 getString(R.string.network_check),
                                 Toast.LENGTH_SHORT).show();
                         return; // 后续代码不执行
                     }

                     // 进度对话框

                     pDialog = ProgressDialog.show(MycontactAddActivity.this, null, "正在加载", false, true);

                     // 开始线程
                     action = "update";
                     contactThread.start();
                 }
             }
         });
    }
    Thread contactThread = new Thread() {
        public void run() {
            // 获取message
            Message msg = handler.obtainMessage();

            HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/Passenger");

            // 发送请求
            DefaultHttpClient client = new DefaultHttpClient();

            try {
                // jsessionid
                SharedPreferences pref = getSharedPreferences("user",
                        Context.MODE_PRIVATE);
                String value = pref.getString("Cookie","");
                BasicHeader header = new BasicHeader("Cookie", value);
                post.setHeader(header);

                // 请求参数
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("姓名",  add_contact_name.getText().toString()));
                params.add(new BasicNameValuePair("证件类型", Spinner_type.getSelectedItem().toString()));
                params.add(new BasicNameValuePair("证件号码",add_contact_num.getText().toString() ));
                params.add(new BasicNameValuePair("乘客类型", Spinner_type_man.getSelectedItem().toString()));
                params.add(new BasicNameValuePair("电话", add_contact_tel.getText().toString()));
                params.add(new BasicNameValuePair("action", action));

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
                post.setEntity(entity);

                // 超时设置
                client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONSTANT.REQUEST_TIMEOUT);
                client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, CONSTANT.SO_TIMEOUT);HttpResponse response = client.execute(post);

                // 处理结果
                if (response.getStatusLine().getStatusCode() == 200) {

                    Gson gson = new GsonBuilder().create();
                    String result = gson.fromJson(EntityUtils.toString(response.getEntity()), String.class);
                    // 发送消息
                    msg.obj = result;
                    msg.what = 1;

                } else {
                    msg.what = 2;
                }

                client.getConnectionManager().shutdown();

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                msg.what = 2;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                msg.what = 2;
            } catch (IOException e) {
                e.printStackTrace();
                msg.what = 2;
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                msg.what = 2;
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                msg.what = 3; // 重新登录
            }

            // 发送消息
            handler.sendMessage(msg);

        };
    };

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 关闭对话框
            if (pDialog != null) {
                pDialog.dismiss();
            }

            switch (msg.what) {
                case 1:
                    String result = (String) msg.obj;
                    if ("1".equals(result)) {
                        Toast.makeText(MycontactAddActivity.this, "添加成功",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else if ("-1".equals(result)) {
                        Toast.makeText(MycontactAddActivity.this, "添加失败，请重试",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    Toast.makeText(MycontactAddActivity.this, "服务器错误，请重试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MycontactAddActivity.this, "请重新登录",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.my_contact,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {

            case R.id.mn_contact_add:
                ContentResolver cr = getContentResolver();
                Cursor c = cr.query(ContactsContract.Contacts.CONTENT_URI, new String[] { "_id", "display_name" }, null, null, null);

                List<String> contacts = new ArrayList<String>();
                while (c.moveToNext()) {
                    String _id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    String display_name = c.getString(c.getColumnIndex("display_name"));
                    // 查找电话
                    Cursor c2 = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { _id + "" }, null);
                    String number = null;
                    while (c2.moveToNext()){
                        number = c2.getString(c2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    c2.close();
//                    Log.v("hehe","name="+display_name);
//                    Log.v("hehe","num="+number);
                    contacts.add(display_name + "(" + number+ ")");

                }
                c.close();

                if (contacts.size() == 0) {
                    new AlertDialog.Builder(MycontactAddActivity.this)
                            .setTitle("请选择").setMessage("通讯录为空")
                            .setNegativeButton("取消", null).show();
                } else {

                    final String[] items = new String[contacts.size()];
                    contacts.toArray(items);

                    // AlertDialog
                    new AlertDialog.Builder(MycontactAddActivity.this)
                            .setTitle("请选择")
                            .setItems(items,  new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    add_contact_name.setText(items[which].split("\\(")[0]);
                                    add_contact_tel.setText(items[which].split("\\(")[1].split("\\)")[0]);

                                    dialog.dismiss();
                                }
                            }).setNegativeButton("取消", null).show();
                }

                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
