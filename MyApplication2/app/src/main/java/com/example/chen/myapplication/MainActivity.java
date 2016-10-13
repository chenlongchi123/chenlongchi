package com.example.chen.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.client.ServiceManager;
import com.examplechen.my.MycontactnewActivity;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {
    ViewPager pager = null;
    TextView tvTicket = null;
    TextView tvOrder = null;
    TextView tvMy = null;
    TextView tvAdd=null;
    TextView tvExit=null;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    //存放Fragment
    private ArrayList<Fragment> fragmentArrayList;

    //管理Fragment
    private FragmentManager fragmentManager;


    @SuppressWarnings("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Start the service
        ServiceManager serviceManager = new ServiceManager(this);
        serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        initLocation();
        mLocationClient.start();
        bindViews();
        InitFragment();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ico);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        tvAdd.setOnClickListener(new MyMapOnClickListener());
        tvExit.setOnClickListener(new MyExitOnClickListener());
//        //导航条
//        final android.support.v7.app.ActionBar bar = getSupportActionBar();
//        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);//设计模式


//  获取ViewPager
        pager = (ViewPager) findViewById(R.id.pager);
        //绑定
//        pager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));
        pager.setAdapter(new MFragmentPagerAdapter(fragmentManager, fragmentArrayList));
        pager.setCurrentItem(0);
        //事件处理
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageSelected(int arg0) {
//                bar.setSelectedNavigationItem(arg0);
                switch (arg0) {
                    case 0:
                        pager.setCurrentItem(0);
                        tvTicket.setBackgroundResource(R.drawable.cab_background_top_mainbar);
                        tvOrder.setBackgroundResource(R.color.possible_result_points);
                        tvMy.setBackgroundResource(R.color.orange);
                        break;
                    case 1:
                        pager.setCurrentItem(1);
                        tvOrder.setBackgroundResource(R.drawable.cab_background_top_mainbar);
                        tvTicket.setBackgroundResource(R.color.result_points);
                        tvMy.setBackgroundResource(R.color.orange);
                        break;
                    case 2:
                        pager.setCurrentItem(2);
                        tvMy.setBackgroundResource(R.drawable.cab_background_top_mainbar);
                        tvTicket.setBackgroundResource(R.color.result_points);
                        tvOrder.setBackgroundResource(R.color.possible_result_points);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
//        //添加
//        bar.addTab(bar.newTab().setText("车票").setTabListener((android.support.v7.app.ActionBar.TabListener) new MyTabListener()));
//        bar.addTab(bar.newTab().setText("订单").setTabListener((android.support.v7.app.ActionBar.TabListener) new MyTabListener()));
//        bar.addTab(bar.newTab().setText("我的").setTabListener((android.support.v7.app.ActionBar.TabListener) new MyTabListener()));
    }

    private void bindViews() {
        tvMy = (TextView) findViewById(R.id.tvMy);
        tvOrder = (TextView) findViewById(R.id.tvOrder);
        tvTicket = (TextView) findViewById(R.id.tvTicket);
        tvTicket.setOnClickListener(new MyOnClickListener());
        tvMy.setOnClickListener(new MyOnClickListener());
        tvOrder.setOnClickListener(new MyOnClickListener());
        tvAdd=(TextView)findViewById(R.id.tvAdd);
        tvExit=(TextView)findViewById(R.id.tvExit);


    }

    //重置所有文本的选中状态
    private void setSelected() {
        tvTicket.setSelected(false);
        tvMy.setSelected(false);
        tvOrder.setSelected(false);
    }

    /**
     * 初始化Fragment，并添加到ArrayList中
     */
    private void InitFragment(){
        fragmentArrayList = new ArrayList<Fragment>();
        fragmentArrayList.add(new TicketFragment());
        fragmentArrayList.add(new OrderFragment());
        fragmentArrayList.add(new MyFragment());

        fragmentManager = getSupportFragmentManager();

    }
//    //适配器类
//    class TabFragmentPagerAdapter extends FragmentPagerAdapter {
//        public TabFragmentPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int arg0) {
//            Fragment f = null;
//            switch (arg0) {
//                case 0:
//                    f = new TicketFragment();
//                    break;
//                case 1:
//                    f = new OrderFragment();
//                    break;
//                case 2:
//                    f = new MyFragment();
//                    break;
//            }
//            return f;
//        }
//
//        @Override
//        public int getCount() {
//            return 3;
//        }
//    }
//
//    class MyTabListener implements android.support.v7.app.ActionBar.TabListener {
//
//        @Override
//        public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
//            pager.setCurrentItem(tab.getPosition());
//        }
//
//        @Override
//        public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
//
//        }
//
//        @Override
//        public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {
//
//        }
//    }


    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.tvTicket:
                    pager.setCurrentItem(0);
                    tvTicket.setBackgroundResource(R.drawable.cab_background_top_mainbar);
                    tvOrder.setBackgroundResource(R.color.possible_result_points);
                    tvMy.setBackgroundResource(R.color.orange);
                    break;
                case R.id.tvOrder:
                    pager.setCurrentItem(1);
                    tvOrder.setBackgroundResource(R.drawable.cab_background_top_mainbar);
                    tvTicket.setBackgroundResource(R.color.result_points);
                    tvMy.setBackgroundResource(R.color.orange);
                    break;
                case R.id.tvMy:
                    pager.setCurrentItem(2);
                    tvMy.setBackgroundResource(R.drawable.cab_background_top_mainbar);
                    tvTicket.setBackgroundResource(R.color.result_points);
                    tvOrder.setBackgroundResource(R.color.possible_result_points);
                    break;

            }
        }

    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=2000*10;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
//        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要


        mLocationClient.setLocOption(option);
        option.setIsNeedAddress(true);
    }


    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
//            Log.v("hehe",bdLocation.getAddrStr().toString().split("市")[0]);
            tvAdd.setText(bdLocation.getCity().toString());


        }
    }

    private class MyMapOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,MycontactnewActivity.class);
            startActivity(intent);
        }
    }

    private class MyExitOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            intent.setClass(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    }
}