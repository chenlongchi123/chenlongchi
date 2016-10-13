package com.ticket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chen.myapplication.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketDetailStep2Activity extends Activity {
    ListView lvTicketDetailsStep2;
    List<Map<String, Object>> data;
    TextView tvTicketDetailsStep2Before = null;
    TextView tvTicketDetailsStep2After = null;
    TextView tvTicketDetailsStep2DateTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail_step2);
        lvTicketDetailsStep2=(ListView)findViewById(R.id.lvTicketDetailsStep2);
        tvTicketDetailsStep2Before = (TextView) findViewById(R.id.tvTicketDetailsStep2Before);
        tvTicketDetailsStep2After = (TextView) findViewById(R.id.tvTicketDetailsStep2After);
        tvTicketDetailsStep2DateTitle = (TextView) findViewById(R.id.tvTicketDetailsStep2DateTitle);

        tvTicketDetailsStep2Before.setOnClickListener(new HandlerTicketResultStep1());
        tvTicketDetailsStep2After .setOnClickListener(new HandlerTicketResultStep1());
        data = new ArrayList<Map<String, Object>>();
        Map<String,Object> row1=new HashMap<String, Object>();
        row1.put("seatName","软座");
        row1.put("seatNum","300张");
        row1.put("seatPrice","¥199");
        data.add(row1);
        Map<String,Object> row2=new HashMap<String, Object>();
        row2.put("seatName","硬座");
        row2.put("seatNum","88张");
        row2.put("seatPrice","¥99");
        data.add(row2);
        Map<String,Object> row3=new HashMap<String, Object>();
        row3.put("seatName","无座");
        row3.put("seatNum","500张");
        row3.put("seatPrice","¥99");
        data.add(row3);
        lvTicketDetailsStep2.setAdapter(new TicketDetailStep2Adapter());
    }
    class TicketDetailStep2Adapter extends BaseAdapter{

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //如果组件对象不存在，创建；存在就取出
            ViewHolder holder=null;
            if(convertView==null){
                holder=new ViewHolder();
                //创建contentView加载行布局
                convertView= LayoutInflater.from(TicketDetailStep2Activity.this)
                        .inflate(R.layout.item_ticket_details_step2,null);
                holder.tvTicketDetailsStep2SeatName=(TextView)convertView
                        .findViewById(R.id.tvTicketDetailsStep2SeatName);
                holder.tvTicketDetailsStep2SeatNum=(TextView)convertView
                        .findViewById(R.id.tvTicketDetailsStep2SeatNum);
                holder.tvTicketDetailsStep2SeatPrice=(TextView)convertView
                        .findViewById(R.id.tvTicketDetailsStep2SeatPrice);
                holder.btnTicketDetailsStep2Order=(Button) convertView
                        .findViewById(R.id.btnTicketDetailsStep2Order);
                convertView.setTag(holder);

            }else {
                holder=(ViewHolder)convertView.getTag();
            }
            //赋值
            holder.tvTicketDetailsStep2SeatName.setText(data.get(position).get("seatName").toString());
            holder.tvTicketDetailsStep2SeatNum.setText(data.get(position).get("seatNum").toString());
            holder.tvTicketDetailsStep2SeatPrice.setText(data.get(position).get("seatPrice").toString());
            holder.btnTicketDetailsStep2Order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(TicketDetailStep2Activity.this,
                            TicketPassengerStep3Activity.class);
                    startActivity(intent);
                }
            });
            return convertView;
        }
    }
    class ViewHolder{

        TextView tvTicketDetailsStep2SeatName;
        TextView tvTicketDetailsStep2SeatNum;
        TextView tvTicketDetailsStep2SeatPrice;
        Button btnTicketDetailsStep2Order;
    }
    class HandlerTicketResultStep1 implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Calendar c = Calendar.getInstance();

            // 获取选中日期
            String oldDateFrom = tvTicketDetailsStep2DateTitle.getText()
                    .toString();
            int oldYear = Integer
                    .parseInt(oldDateFrom.split(" ")[0].split("-")[0]);
            int oldMonthOfYear = Integer.parseInt(oldDateFrom.split(" ")[0]
                    .split("-")[1]) - 1;
            int oldDayOfMonth = Integer.parseInt(oldDateFrom.split(" ")[0]
                    .split("-")[2]);
            c.set(oldYear, oldMonthOfYear, oldDayOfMonth);

            switch (v.getId()) {
                case R.id.tvTicketDetailsStep2Before:
                    // 前一天
                    c.add(Calendar.DAY_OF_MONTH, -1);
                    break;
                case R.id.tvTicketDetailsStep2After :
                    // 后一天
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    break;
            }

            // 更新选中日期
            String weekDay = DateUtils.formatDateTime(
                    TicketDetailStep2Activity.this, c.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_WEEKDAY
                            | DateUtils.FORMAT_ABBREV_WEEKDAY);

            tvTicketDetailsStep2DateTitle.setText(c.get(Calendar.YEAR) + "-"
                    + (c.get(Calendar.MONTH) + 1) + "-"
                    + c.get(Calendar.DAY_OF_MONTH) + " " + weekDay);
        }
    }


}
