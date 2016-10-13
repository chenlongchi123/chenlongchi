package com.examplechen.my;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

import static android.R.attr.action;

public class MycontactEditActivity extends AppCompatActivity {
    ListView lvEditList;
    Button btnEdit=null;
    List<Map<String,Object>> data=null;
    SimpleAdapter adapter=null;
    ProgressDialog pDialog = null;
    String action = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycontact_edit);
        lvEditList=(ListView)findViewById(R.id.lvEditList);
        btnEdit=(Button)findViewById(R.id.btnEdit);
        android.support.v7.app.ActionBar bar=getSupportActionBar();
        bar.setTitle("更改信息");
        //接收数据
        Intent intent=getIntent();
        Map<String,Object> contact=(Map<String,Object>)intent.getSerializableExtra("row");
        //数据部分
        data=new ArrayList<Map<String, Object>>();
        //row1姓名
        Map<String,Object> row1= new HashMap<String,Object>();
        String name=(String)contact.get("name");
        row1.put("key1","姓名");
        row1.put("key2",name.split("\\(")[0]);
        row1.put("key3",R.drawable.forward_25);
        data.add(row1);
        //row2证件类型
        Map<String,Object> row2= new HashMap<String,Object>();
        String id=(String)contact.get("id");
        row2.put("key1","证件类型");
        row2.put("key2",id.split(":")[0]);
        row2.put("key3",null);
        data.add(row2);
        //row3证件类型
        Map<String,Object> row3= new HashMap<String,Object>();
        row3.put("key1","证件号码");
        row3.put("key2",id.split(":")[1]);
        row3.put("key3",null);
        data.add(row3);
        //row3乘客类型
        Map<String,Object> row4= new HashMap<String,Object>();
        row4.put("key1","乘客类型");
        row4.put("key2",name.split("\\(")[1].split("\\)")[0]);
        row4.put("key3",R.drawable.forward_25);
        data.add(row4);
        //row1电话号码
        Map<String,Object> row5= new HashMap<String,Object>();
        String tel=(String)contact.get("tel");
        row5.put("key1","电话号码");
        row5.put("key2",tel.split(":")[1]);
        row5.put("key3",R.drawable.forward_25);
        data.add(row5);
        //适配器
        adapter=new SimpleAdapter(MycontactEditActivity.this,data,R.layout.activity_item_mycontact_edit,
                new String[]{"key1","key2","key3"},new int[]{R.id.tvEdit1,R.id.tvEdit2,R.id.img1});
        lvEditList.setAdapter(adapter);

        lvEditList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                switch (position){
                    case 0:
                        final EditText edtName=new EditText(MycontactEditActivity.this);
                        edtName.setText((String)(data.get(position).get("key2")));
                        edtName.selectAll();//默认全选中
                        new AlertDialog.Builder(MycontactEditActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle("请输入姓名")
                                .setView(edtName)
                                .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog,int which){
                                        // 验证
                                        String name=edtName.getText().toString();
                                        if (TextUtils.isEmpty(name)){
                                            //设置对话框不能自动关闭
                                            setClosable(dialog,false);
                                            edtName.setError("请输入姓名");
                                            edtName.requestFocus();
                                        }else {
                                            data.get(position).put("key2",edtName.getText().toString());
                                        //更新listView
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }).setNegativeButton("取消",new DialogInterface.OnClickListener(){
                           public void onClick(DialogInterface dialog,int which){
                               setClosable(dialog,true);
                           }
                        }).show();
                        break;
                    case 3:
                        final String[] types=new String[]{"成人","学生","儿童","其他"};
                        String key2=(String)(data.get(position).get("key2"));
                        int idx=0;
                        for(int i=0;i<types.length;i++){
                            if(types[i].equals(key2)){
                                idx=i;
                                break;
                            }
                        }
                        new  AlertDialog.Builder(MycontactEditActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("请选择乘客类型")
                                .setSingleChoiceItems(types,idx,new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog,int which){
                                        data.get(position).put("key2",types[which]);
                                        adapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("取消",null).show();
                        break;
                    case 4:
                        final EditText edtTel=new EditText(MycontactEditActivity.this);
                        edtTel.setText((String)(data.get(position).get("key2")));
                        edtTel.selectAll();//默认全选中
                        new AlertDialog.Builder(MycontactEditActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle("请输入电话号码")
                                .setView(edtTel)
                                .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog,int which){
                                        // 验证
                                        String name=edtTel.getText().toString();
                                        if (TextUtils.isEmpty(name)){
                                            //设置对话框不能自动关闭
                                            setClosable(dialog,false);
                                            edtTel.setError("请输入电话号码");
                                            edtTel.requestFocus();
                                        }else {
                                            data.get(position).put("key2",edtTel.getText().toString());
                                            //更新listView
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }).setNegativeButton("取消",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog,int which){
                                setClosable(dialog,true);
                            }
                        }).show();
                        break;
                }
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // 1.数据保存到服务器上
                if (!NetUtils.check(MycontactEditActivity.this)) {
                    Toast.makeText(MycontactEditActivity.this,
                            getString(R.string.network_check),
                            Toast.LENGTH_SHORT).show();
                    return; // 后续代码不执行
                }

                // 进度对话框

                pDialog=ProgressDialog.show(MycontactEditActivity.this,null,"正在加载",false,true);

                // 开始线程
                action = "update";
                contactThread.start();
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
                String value = pref.getString("Cookie", "");
                BasicHeader header = new BasicHeader("Cookie", value);
                post.setHeader(header);

                // 请求参数
                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair("姓名", (String) data.get(0)
                        .get("key2")));
                params.add(new BasicNameValuePair("证件类型", (String) data.get(1)
                        .get("key2")));
                params.add(new BasicNameValuePair("证件号码", (String) data.get(2)
                        .get("key2")));
                params.add(new BasicNameValuePair("乘客类型", (String) data.get(3)
                        .get("key2")));
                params.add(new BasicNameValuePair("电话", (String) data.get(4)
                        .get("key2")));
                params.add(new BasicNameValuePair("action", action));

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
                    String info = "修改";
                    if ("remove".equals(action)) {
                        info = "删除";
                    }

                    if ("1".equals(result)) {

                        Toast.makeText(MycontactEditActivity.this, info + "成功",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else if ("-1".equals(result)) {
                        Toast.makeText(MycontactEditActivity.this, info + "失败，请重试",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    Toast.makeText(MycontactEditActivity.this, "服务器错误，请重试",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MycontactEditActivity.this, "请重新登录",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };
    @Override
    protected void onResume(){
        super.onResume();
        //访问服务器，更新数据
        Toast.makeText(MycontactEditActivity.this,"刷新",Toast.LENGTH_SHORT).show();
    }

    private  void  setClosable(DialogInterface dialog,boolean b){
        Field field;
        try{
            field=dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog,b);
        }catch (NoSuchFieldException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.my_contact_edit, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mn_removeuser_contact:

                // 开始线程
                action = "remove";
                contactThread.start();

                break;
        }


        return super.onOptionsItemSelected(item);
    }

}
