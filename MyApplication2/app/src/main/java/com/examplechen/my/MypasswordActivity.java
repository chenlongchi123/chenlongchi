package com.examplechen.my;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bean.Account;
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
import java.util.ArrayList;
import java.util.List;

public class MypasswordActivity extends AppCompatActivity {
    EditText etPassword1;
    EditText etPassword2;
    Button btnSave;
    String action = "";
    ProgressDialog pDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypassword);
        etPassword1=(EditText)findViewById(R.id.etPassword1);
        etPassword2=(EditText)findViewById(R.id.etPassword2);
        btnSave=(Button)findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // 1.数据保存到服务器上
                if (!NetUtils.check(MypasswordActivity.this)) {
                    Toast.makeText(MypasswordActivity.this,
                            getString(R.string.network_check),
                            Toast.LENGTH_SHORT).show();
                    return; // 后续代码不执行
                }

                // 进度对话框

                pDialog= ProgressDialog.show(MypasswordActivity.this,null,"正在加载",false,true);

                // 开始线程
                contactThread.start();
                finish();
            }
        });
    }
    Thread contactThread = new Thread() {
        public void run() {
            // 获取message
            Message msg = handlers.obtainMessage();

            HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/AccountPassword");

            // 发送请求
            DefaultHttpClient client = new DefaultHttpClient();

            try {
                // jsessionid
                SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
                String value = pref.getString("Cookie", "");
                BasicHeader header = new BasicHeader("Cookie", value);
                post.setHeader(header);

                // 请求参数
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

                params.add(new BasicNameValuePair("oldPassword", etPassword2.getText().toString()));
                params.add(new BasicNameValuePair("action", "update"));

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
                post.setEntity(entity);

                // 超时设置
                client.getParams().setParameter(
                        CoreConnectionPNames.CONNECTION_TIMEOUT,
                        CONSTANT.REQUEST_TIMEOUT);
                client.getParams().setParameter(
                        CoreConnectionPNames.SO_TIMEOUT, CONSTANT.SO_TIMEOUT);

                HttpResponse response = client.execute(post);

                // 处理结果
                if (response.getStatusLine().getStatusCode() == 200) {

                    String result ="1";
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
            handlers.sendMessage(msg);

        };
    };
    Handler handlers = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 关闭对话框
            if (pDialog != null) {
                pDialog.dismiss();
            }

            switch (msg.what) {
                case 1:
                    String result = (String) msg.obj;
                    String info = "修改";
                    if ("1".equals(result)) {

                        Toast.makeText(MypasswordActivity.this, info + "成功",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else if ("-1".equals(result)) {
                        Toast.makeText(MypasswordActivity.this, info + "失败，请重试",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    Toast.makeText(MypasswordActivity.this, "服务器错误，请重试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MypasswordActivity.this, "请重新登录",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };
}
