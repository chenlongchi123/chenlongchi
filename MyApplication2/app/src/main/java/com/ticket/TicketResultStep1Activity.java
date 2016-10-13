package com.ticket;

        import java.io.IOException;
        import java.io.Serializable;
        import java.io.UnsupportedEncodingException;
        import java.util.ArrayList;
        import java.util.Calendar;
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

        import com.bean.Seat;
        import com.bean.Train;
        import com.example.chen.myapplication.R;
        import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;
        import com.google.gson.JsonSyntaxException;
        import com.utils.CONSTANT;


        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.text.format.DateUtils;
        import android.view.Menu;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.AdapterView;
        import android.widget.AdapterView.OnItemClickListener;
        import android.widget.ListView;
        import android.widget.SimpleAdapter;
        import android.widget.TextView;
        import android.widget.Toast;

public class TicketResultStep1Activity extends Activity {
    ListView lvTicketResultStep1 = null;
    TextView tvTicketResultStep1Before = null;
    TextView tvTicketResultStep1After = null;
    TextView tvTicketResultStep1DateTitle = null;
    ProgressDialog pDialog = null;
    List<Map<String, Object>> data = null;
    SimpleAdapter adapter = null;
    Train[] trains = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_result_step1);

        lvTicketResultStep1 = (ListView) findViewById(R.id.lvTicketResultStep1);
        tvTicketResultStep1Before = (TextView) findViewById(R.id.tvTicketResultStep1Before);
        tvTicketResultStep1After = (TextView) findViewById(R.id.tvTicketResultStep1After);
        tvTicketResultStep1DateTitle = (TextView) findViewById(R.id.tvTicketResultStep1DateTitle);

        tvTicketResultStep1Before
                .setOnClickListener(new HandlerTicketResultStep1());
        tvTicketResultStep1After
                .setOnClickListener(new HandlerTicketResultStep1());

        // 日期
        tvTicketResultStep1DateTitle.setText(getIntent().getStringExtra(
                "startTrainDate"));
        // from - to
        // ...

        // 数据
        data = new ArrayList<Map<String, Object>>();

         Map<String, Object> row1 = new HashMap<String, Object>();
         row1.put("trainNo", "G108");
         row1.put("flg1", R.drawable.flg_shi);
         row1.put("flg2", R.drawable.flg_guo);
         row1.put("timeFrom", "07:00");
         row1.put("timeTo", "14:39(0日)");
         row1.put("seat1", "高级软卧:100");
         row1.put("seat2", "硬座:8");
         row1.put("seat3", "一等座:20");
         row1.put("seat4", "商务座:200");
         data.add(row1);

         Map<String, Object> row2 = new HashMap<String, Object>();
         row2.put("trainNo", "D1");
         row2.put("flg1", R.drawable.flg_shi);
         row2.put("flg2", R.drawable.flg_zhong);
         row2.put("timeFrom", "09:00");
         row2.put("timeTo", "12:39(0日)");
         row2.put("seat1", "高级软卧:100");
         row2.put("seat2", "硬座:8");
         row2.put("seat3", "一等座:20");
         row2.put("seat4", "商务座:200");
         data.add(row2);

         Map<String, Object> row3 = new HashMap<String, Object>();
         row3.put("trainNo", "K7777");
         row3.put("flg1", R.drawable.flg_guo);
         row3.put("flg2", R.drawable.flg_guo);
         row3.put("timeFrom", "15:00");
         row3.put("timeTo", "12:39(1日)");
         row3.put("seat1", "高级软卧:55");
         row3.put("seat2", "硬座:77");
         row3.put("seat3", "一等座:33");
         data.add(row3);

        adapter = new SimpleAdapter(this, data,
                R.layout.item_ticket_result_step1, new String[] { "trainNo",
                "flg1", "flg2", "timeFrom", "timeTo", "seat1", "seat2",
                "seat3", "seat4" }, new int[] {
                R.id.tvTicketResultStep1TrainNo,
                R.id.imgTicketResultStep1Flg1,
                R.id.imgTicketResultStep1Flg2,
                R.id.tvTicketResultStep1TimeFrom,
                R.id.tvTicketResultStep1TimeTo,
                R.id.tvTicketResultStep1Seat1,
                R.id.tvTicketResultStep1Seat2,
                R.id.tvTicketResultStep1Seat3,
                R.id.tvTicketResultStep1Seat4, });

        lvTicketResultStep1.setAdapter(adapter);

        lvTicketResultStep1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(TicketResultStep1Activity.this, TicketDetailStep2Activity.class);
                startActivity(intent);
            }
        });

    }

    class HandlerTicketResultStep1 implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Calendar c = Calendar.getInstance();

            // 获取选中日期
            String oldDateFrom = tvTicketResultStep1DateTitle.getText()
                    .toString();
            int oldYear = Integer
                    .parseInt(oldDateFrom.split(" ")[0].split("-")[0]);
            int oldMonthOfYear = Integer.parseInt(oldDateFrom.split(" ")[0]
                    .split("-")[1]) - 1;
            int oldDayOfMonth = Integer.parseInt(oldDateFrom.split(" ")[0]
                    .split("-")[2]);
            c.set(oldYear, oldMonthOfYear, oldDayOfMonth);

            switch (v.getId()) {
                case R.id.tvTicketResultStep1Before:
                    // 前一天
                    c.add(Calendar.DAY_OF_MONTH, -1);
                    break;
                case R.id.tvTicketResultStep1After:
                    // 后一天
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    break;
            }

            // 更新选中日期
            String weekDay = DateUtils.formatDateTime(
                    TicketResultStep1Activity.this, c.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_WEEKDAY
                            | DateUtils.FORMAT_ABBREV_WEEKDAY);

            tvTicketResultStep1DateTitle.setText(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-"
                    + c.get(Calendar.DAY_OF_MONTH) + " " + weekDay);


        }

    }





}
