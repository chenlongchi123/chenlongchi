package com.example.chen.myapplication;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bean.Account;
import com.examplechen.my.MyaccountActivity;
import com.examplechen.my.MycontactActivity;
import com.examplechen.my.MycontactEditActivity;
import com.examplechen.my.MypasswordActivity;
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

import static android.R.attr.value;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {

    ListView listView;
    String password=null;

    public MyFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my, container, false);
    }
    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);

            listView=(ListView)getActivity().findViewById(R.id.listView);

            //每行布局
            //数据
            String[] data=getResources().getStringArray(R.array.my_list_data);
            //适配器
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,data);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent=new Intent();
                    switch (position) {
                        case 0:
                            intent.setClass(getActivity(), MycontactActivity.class);
                            startActivity(intent);
                            break;
                        case 1:
                            intent.setClass(getActivity(), MyaccountActivity.class);
                            startActivity(intent);
                            break;
                        case 2:
                            final EditText edtName=new EditText(getActivity());
                            new AlertDialog.Builder(getActivity())
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setTitle("请输入旧密码")
                                    .setView(edtName)
                                    .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog,int which){
                                            // 验证
                                            String name=edtName.getText().toString();
                                            if (TextUtils.isEmpty(name)){
                                                //设置对话框不能自动关闭
                                                setClosable(dialog,false);
                                                edtName.setError("请输入密码");
                                                edtName.requestFocus();
                                            }
                                            String oldPassword=edtName.getText().toString();
                                            check(oldPassword);
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


        }

    private void check(final String oldPassword) {

        new Thread() {
            public void run() {
                Message msg = handler.obtainMessage();
                HttpPost post = new HttpPost(CONSTANT.HOST + "/otn/Account");
                // 发送请求
                DefaultHttpClient client = new DefaultHttpClient();
                try {
                    // jsessionid
                    SharedPreferences pref = getContext().getSharedPreferences("user",
                            Context.MODE_PRIVATE);
                    String value = pref.getString("Cookie", "");
                    // 1
                    BasicHeader header = new BasicHeader("Cookie", value);
                    post.setHeader(header);
                    // 请求参数
                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair("action", "query"));
                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,
                            "UTF-8");
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

                        String json = EntityUtils.toString(response.getEntity());
                        Gson gson = new GsonBuilder().create();
                        Account account  = gson.fromJson(json, Account.class);
                        password=account.getPassword();
                        if (oldPassword.equals(password)){
                        // 发送消息
                        msg.obj =password;
                        msg.what = 1;}else {
                            msg.what=2;
                        }

                    }
                    client.getConnectionManager().shutdown();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    msg.what = 2;
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    msg.what = 2;
                    e.printStackTrace();
                } catch (IOException e) {
                    msg.what = 2;
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    msg.what = 2;
                    e.printStackTrace();
                } catch (JsonSyntaxException e) {
                    msg.what = 2;
                    e.printStackTrace();
                }
                // 发送消息
                handler.sendMessage(msg);
            };
        }.start();
    }
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case 1:
                    Intent intent=new Intent();
                    intent.setClass(getActivity(), MypasswordActivity.class);
                    startActivity(intent);
                   password = (String)msg.obj;
                    Log.v("hehe",password.toString());

                    break;
                case 2:
                    Toast.makeText(getActivity(), "密码错误请重新输入",
                            Toast.LENGTH_SHORT).show();
                    break;

            }
        };
    };

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
}


