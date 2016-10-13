package com.example.chen.myapplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.order.OrderPayDoneActivity;
import com.order.OrderWaitPayActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class OrderFragment extends Fragment {
    TextView tvOrderWaitPay;
    TextView tvOrderAll;
    ListView lvOrder;
    List<Map<String, Object>> data;
    OrderAdapter adapter = null;

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        tvOrderWaitPay = (TextView) getActivity().findViewById(
                R.id.tvOrderWaitPay);
        tvOrderAll = (TextView) getActivity().findViewById(R.id.tvOrderAll);
        lvOrder = (ListView) getActivity().findViewById(R.id.lvOrder);

        tvOrderWaitPay.setOnClickListener(new OrderHandler());
        tvOrderAll.setOnClickListener(new OrderHandler());

        data = new ArrayList<Map<String, Object>>();
        Map<String, Object> row1 = new HashMap<String, Object>();
        row1.put("orderId", "订单编号:20143Yg");
        row1.put("orderStatus", "未支付");
        row1.put("orderTrainNo", "G108");
        row1.put("orderDateFrom", "2014-8-18");
        row1.put("orderStationFrom", "北京-上海 2人");
        row1.put("orderPrice", "￥1000.5元");
        row1.put("orderFlg", R.drawable.forward_25);
        data.add(row1);

        adapter = new OrderAdapter();
        lvOrder.setAdapter(adapter);

        lvOrder.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String orderStatus = (String) data.get(position).get(
                        "orderStatus");

                if ("已支付".equals(orderStatus)) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), OrderPayDoneActivity.class);
                    startActivity(intent);
                } else if ("未支付".equals(orderStatus)) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), OrderWaitPayActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    class OrderAdapter extends BaseAdapter {

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
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();

                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.item_order_list, null);

                holder.tvOrderId = (TextView) convertView
                        .findViewById(R.id.tvOrderId);
                holder.tvOrderStatus = (TextView) convertView
                        .findViewById(R.id.tvOrderStatus);
                holder.tvOrderTrainNo = (TextView) convertView
                        .findViewById(R.id.tvOrderTrainNo);
                holder.tvOrderDateFrom = (TextView) convertView
                        .findViewById(R.id.tvOrderDateFrom);
                holder.tvOrderStationFrom = (TextView) convertView
                        .findViewById(R.id.tvOrderStationFrom);
                holder.tvOrderPrice = (TextView) convertView
                        .findViewById(R.id.tvOrderPrice);
                holder.imgOrderFlg = (ImageView) convertView
                        .findViewById(R.id.imgOrderFlg);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 赋值
            holder.tvOrderId.setText(data.get(position).get("orderId")
                    .toString());

            holder.tvOrderStatus.setText(data.get(position).get("orderStatus")
                    .toString());
            if ("未支付".equals(holder.tvOrderStatus.getText().toString())) {
                holder.tvOrderStatus.setTextColor(getResources().getColor(
                        R.color.orange));

            } else if ("已支付".equals(holder.tvOrderStatus.getText().toString())) {
                holder.tvOrderStatus.setTextColor(getResources().getColor(
                        R.color.blue));
            } else if ("已取消".equals(holder.tvOrderStatus.getText().toString())) {
                holder.tvOrderStatus.setTextColor(getResources().getColor(
                        R.color.grey));
            }

            holder.tvOrderTrainNo.setText(data.get(position)
                    .get("orderTrainNo").toString());
            holder.tvOrderDateFrom.setText(data.get(position)
                    .get("orderDateFrom").toString());
            holder.tvOrderStationFrom.setText(data.get(position)
                    .get("orderStationFrom").toString());
            holder.tvOrderPrice.setText(data.get(position).get("orderPrice")
                    .toString());

            Integer resId = (Integer) (data.get(position).get("orderFlg"));
            if (resId != null) {
                holder.imgOrderFlg.setImageDrawable(getResources().getDrawable(
                        resId));
            } else {
                holder.imgOrderFlg.setImageDrawable(null);
            }

            return convertView;
        }
    }

    class ViewHolder {
        TextView tvOrderId;
        TextView tvOrderStatus;
        TextView tvOrderTrainNo;
        TextView tvOrderDateFrom;
        TextView tvOrderStationFrom;
        TextView tvOrderPrice;
        ImageView imgOrderFlg;
    }

    class OrderHandler implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            data.clear();
            switch (v.getId()) {
                case R.id.tvOrderWaitPay:
                    tvOrderWaitPay
                            .setBackgroundResource(R.drawable.cab_background_top_mainbar);
                    tvOrderAll.setBackgroundResource(0);

                    Map<String, Object> row1 = new HashMap<String, Object>();
                    row1.put("orderId", "订单编号:20143Yg");
                    row1.put("orderStatus", "未支付");
                    row1.put("orderTrainNo", "G108");
                    row1.put("orderDateFrom", "2014-8-18");
                    row1.put("orderStationFrom", "北京-上海 2人");
                    row1.put("orderPrice", "￥1000.5元");
                    row1.put("orderFlg", R.drawable.forward_25);
                    data.add(row1);

                    adapter.notifyDataSetChanged();
                    break;
                case R.id.tvOrderAll:
                    tvOrderAll
                            .setBackgroundResource(R.drawable.cab_background_top_mainbar);
                    tvOrderWaitPay.setBackgroundResource(0);

                    Map<String, Object> row2 = new HashMap<String, Object>();
                    row2.put("orderId", "订单编号:20143Yg");
                    row2.put("orderStatus", "未支付");
                    row2.put("orderTrainNo", "G108");
                    row2.put("orderDateFrom", "2014-8-18");
                    row2.put("orderStationFrom", "北京-上海 2人");
                    row2.put("orderPrice", "￥1000.5元");
                    row2.put("orderFlg", R.drawable.forward_25);
                    data.add(row2);

                    Map<String, Object> row3 = new HashMap<String, Object>();
                    row3.put("orderId", "订单编号:20143Yg");
                    row3.put("orderStatus", "已支付");
                    row3.put("orderTrainNo", "G108");
                    row3.put("orderDateFrom", "2014-8-18");
                    row3.put("orderStationFrom", "北京-上海 2人");
                    row3.put("orderPrice", "￥1000.5元");
                    row3.put("orderFlg", R.drawable.forward_25);
                    data.add(row3);

                    Map<String, Object> row4 = new HashMap<String, Object>();
                    row4.put("orderId", "订单编号:20143Yg");
                    row4.put("orderStatus", "已取消");
                    row4.put("orderTrainNo", "G108");
                    row4.put("orderDateFrom", "2014-8-18");
                    row4.put("orderStationFrom", "北京-上海 2人");
                    row4.put("orderPrice", "￥1000.5元");
                    row4.put("orderFlg", null);
                    data.add(row4);

                    adapter.notifyDataSetChanged();

                    break;
            }
        }

    }

}
