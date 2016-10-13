package com.example.chen.myapplication;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.utils.CONSTANT;
import com.utils.Md5Utils;
import com.utils.NetUtils;
import com.utils.ContentHandler;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;


public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    TextView tvForget;
    EditText etAccount;
    EditText etPassword;
    CheckBox ckBox;
    ProgressDialog pDialog=null;


    Handler handler=new Handler(){
        //回调方法
        public void handleMessage(Message msg){
            //关闭对话框
            if (pDialog!=null){
                pDialog.dismiss();
            }
            switch (msg.what){
                case 1:
                    String jsessionid=(String)msg.obj;

                    int result= msg.arg1;
                    if (result==0){
                        etAccount.selectAll();
                        etAccount.setError("用户名或密码错误");
                        etAccount.requestFocus();
                    }else {
                        SharedPreferences pref=getSharedPreferences("user",Context.MODE_PRIVATE);
                        Editor editor=pref.edit();
                        //记录jessionid

                        editor.putString("Cookie",jsessionid);
                        //记录用户名或密码
                        if (ckBox.isChecked()){
                            editor.putString("username",etAccount.getText().toString());
                            editor.putString("password",etPassword.getText().toString());
                        }
                        else{
                            //清空以前的登录信息
                            editor.remove("username");
                            editor.remove("password");
                        }
                        editor.commit();//最后要提交
                        Intent intent=new Intent();
                        intent.setClass(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        //关闭
                        LoginActivity.this.finish();
                    }
                    break;
                case 2:
                    Toast.makeText(LoginActivity.this,"服务器错误，请重试",Toast.LENGTH_SHORT).show();
                    break;
            }

        };
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin=(Button)findViewById(R.id.Login);
        tvForget=(TextView) findViewById(R.id.tvForget);
        etAccount=(EditText) findViewById(R.id.etAccount);
        etPassword=(EditText) findViewById(R.id.etPassword1);
        ckBox=(CheckBox)findViewById(R.id.ckBox);
        //忘记密码链接
        tvForget.setText(Html.fromHtml("<a href=\"https://kyfw.12306.cn/otn/forgetPassword/initforgetMyPassword\">忘记密码？</a>"));
        tvForget.setMovementMethod(LinkMovementMethod.getInstance());
        //绑定监听器
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(etAccount.getText().toString())){
                    etAccount.setError("请输入用户名");
                    etAccount.requestFocus();
                }
                else if(TextUtils.isEmpty(etPassword.getText().toString())){
                    etPassword.setError("请输入密码");
                    etPassword.requestFocus();//移动光标到EditText处
                }
                else {
                    if(!NetUtils.check(LoginActivity.this)){
                        Toast.makeText(LoginActivity.this,getString(R.string.network_check),Toast.LENGTH_SHORT).show();
                        return;//后续代码不可用
                    }
                    //进度对话框

                    pDialog=ProgressDialog.show(LoginActivity.this,null,"正在连接",false,true);
                    new Thread(new Runnable(){
                        @Override
                        public void  run(){
                            Message msg=handler.obtainMessage();

                            //访问服务器端，验证用户名、密码
                            //发送请求

                            HttpPost post=new  HttpPost(CONSTANT.HOST + "/Login");
                            //设置参数
                            List<BasicNameValuePair> params=new ArrayList<BasicNameValuePair>();
                            params.add(new BasicNameValuePair("username",etAccount.getText().toString()));
                            params.add(new BasicNameValuePair("password", Md5Utils.MD5(etPassword.getText().toString())));
                            UrlEncodedFormEntity entity;
                            try{

                                entity=new UrlEncodedFormEntity(params,"UTF-8");
                                post.setEntity(entity);

                                //超时设置
                                DefaultHttpClient client=new DefaultHttpClient();
                                client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONSTANT.REQUEST_TIMEOUT);
                                client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,CONSTANT.SO_TIMEOUT);
                                HttpResponse response=client.execute(post);
                                if(response.getStatusLine().getStatusCode()==200){
                                    Log.v("hehe","ok");
                                    //打印响应结果
//                                    Log.v("my12306", EntityUtils.toString(response.getEntity()));
                                    //解析xml

                                    StringBuilder builder = new StringBuilder();
                                    HttpEntity httpEntity = response.getEntity();//得到一个http实体


                                    InputStream inputStream = httpEntity.getContent();//得到内容
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                                    String str;
                                    //打印返回的内容（可以看到内容是xml）
                                    while ((str = reader.readLine()) != null)
                                    {
//                                           Log.v("hehe", str);
                                        builder.append(str);
                                    }
                                    //解析数据
//                                    Log.v("hehe", builder.toString());
                                    str=builder.toString();

                                    //得到解析工厂
                                    SAXParserFactory factory = SAXParserFactory.newInstance();
                                    //得到xmlReader
                                    XMLReader xmlReader = factory.newSAXParser().getXMLReader();
                                    //创造一个解析句柄
                                    ContentHandler handle = new ContentHandler();
                                    //reader设置句柄
                                    xmlReader.setContentHandler(handle);
                                    //设置数据源开启解析
                                    try{xmlReader.parse(new InputSource(new StringReader(str)));

                                    }catch (Exception e){e.printStackTrace();}

                                    //记录jessionid
                                    String valus="";
                                    List<Cookie> cookies=client.getCookieStore().getCookies();
                                    for (Cookie cookie:cookies){
                                        if ("JSESSIONID".equals(cookie.getName())){
                                            valus=cookie.getValue();
                                            Log.d("My12306","JSESSIONID:"+valus);
                                            break;
                                        }
                                    }

                                    //发送消息
                                    msg.what=1;

                                    msg.arg1= Integer.parseInt(ContentHandler.s.toString());
//                                    Log.v("kkk", ContentHandler.s.toString());
                                  msg.obj="JSESSIONID="+valus;
                                }else {
                                    msg.what=2;
                                }
                                //关闭连接
                                client.getConnectionManager().shutdown();
                                handler.sendMessage(msg);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (ClientProtocolException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (SAXException e) {
                                e.printStackTrace();
                            } catch (ParserConfigurationException e) {
                                e.printStackTrace();
                            }

//                          //假设验证失败
//                          Message msg=handler.obtainMessage();
//                          msg.obj="用户名或密码错误！";
//                          handler.sendMessage(msg);
                        };
                    }).start();

//                finish();
                }
            }
        });
    }



}
