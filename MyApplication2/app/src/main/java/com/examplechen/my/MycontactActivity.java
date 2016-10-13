package com.examplechen.my;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bean.Passenger;
import com.example.chen.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.utils.CONSTANT;
import com.utils.NetUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MycontactActivity extends AppCompatActivity {
    ListView lvMycontact;
    List<Map<String, Object>> data = null;
    SimpleAdapter adapter = null;
    ProgressDialog pDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycontact);
        //  返回菜单
         ActionBar bar=getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        lvMycontact=(ListView)findViewById(R.id.lvMycontact);
        // 数据
        data = new ArrayList<Map<String, Object>>();

        // 适配器
        // context: 上下文
        // data: 数据
        // resource: 每一行的布局方式
        // from: Map中的key
        // to: 布局中的组件id
        adapter = new SimpleAdapter(this, data, R.layout.activity_item_mycontact,
                new String[] { "name", "id", "tel" }, new int[] {R.id.tvName, R.id.tvId, R.id.tvTel });

        // 绑定
        lvMycontact.setAdapter(adapter);


        // 事件处理
        lvMycontact.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MycontactActivity.this,
                        MycontactEditActivity.class);
                // 传递数据
                intent.putExtra("row", (Serializable) data.get(position)); // Map
                startActivity(intent);
            }
        });

    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        // 访问服务器
        // Toast.makeText(MyContactActivity.this, "刷新",
        // Toast.LENGTH_SHORT).show();

        if (!NetUtils.check(MycontactActivity.this)) {
            Toast.makeText(MycontactActivity.this,
                    getString(R.string.network_check), Toast.LENGTH_SHORT)
                    .show();
            return; // 后续代码不执行
        }

        // 进度对话框
//        pDialog=ProgressDialog.show(MycontactActivity.this,null, "正在加载中...",false, true);
        pDialog=ProgressDialog.show(MycontactActivity.this,null,"正在连接",false,true);

        new Thread() {
            public void run() {
                // 获取message
                Message msg = handler.obtainMessage();

                HttpPost post = new HttpPost(CONSTANT.HOST
                        + "/otn/PassengerList");

                // 发送请求
                DefaultHttpClient client = new DefaultHttpClient();

                try {
                    // jsessionid
                    SharedPreferences pref = getSharedPreferences("user",
                            Context.MODE_PRIVATE);
                    String value = pref.getString("Cookie", "");
                    BasicHeader header = new BasicHeader("Cookie", value);
                    post.setHeader(header);

                    // 超时设置
                    client.getParams().setParameter(
                            CoreConnectionPNames.CONNECTION_TIMEOUT,
                            CONSTANT.REQUEST_TIMEOUT);
                    client.getParams().setParameter(
                            CoreConnectionPNames.SO_TIMEOUT,
                            CONSTANT.SO_TIMEOUT);

                    HttpResponse response = client.execute(post);

                    // 处理结果
                    if (response.getStatusLine().getStatusCode() == 200) {

                        String json = EntityUtils.toString(response.getEntity());

                        Gson gson = new GsonBuilder().create();

                        Passenger[] passengers = gson.fromJson(json,
                                Passenger[].class);

                        // 发送消息
                        msg.obj = passengers;
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
        }.start();
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 关闭对话框
            if (pDialog != null) {
                pDialog.dismiss();
            }

            // 清空data
            data.clear();

            switch (msg.what) {
                case 1:
                    // Passenger[] => data
                    Passenger[] passengers = (Passenger[]) msg.obj;
                    for (Passenger passenger : passengers) {
                        Log.v("hehe",passenger.toString());
                        Map<String, Object> row = new HashMap<String, Object>();
                        row.put("name", passenger.getName() + "(" + passenger.getType() + ")");
                        row.put("id", passenger.getIdType() + ":" + passenger.getId());
                        row.put("tel", "电话:" + passenger.getTel());
                        data.add(row);
                    }
                    adapter.notifyDataSetChanged();

                    break;
                case 2:
                    Toast.makeText(MycontactActivity.this, "服务器错误，请重试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MycontactActivity.this, "请重新登录",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };
    //加载

@Override
    public boolean onCreateOptionsMenu(Menu menu){
    getMenuInflater().inflate(R.menu.my_contact,menu);
    return true;
    }


//返回执行
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
            case R.id.mn_contact_add:
                //跳转添加新用户
                Intent intent=new Intent(MycontactActivity.this,MycontactAddActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
