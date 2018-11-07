package com.alipay.wan.alipaynotification;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private ListView listview;
    private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
    private MyAdapter adapter;
    private TextView mInfoTex;
    private static MainActivity ins;

    private Map<String, Object>map;
    private  LocationReceiver locationReceiver;
    static public String  broadcast_name="location.reportsucc";//this.getResources().getString(R.string.broadcast_name);

    private Calendar cal;
    private String year;
    private String month;
    private String day;
    private String hour;
    private String minute;
    private String second;
    private String my_time_1;
    private String my_time_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //        在Activity创建时注册广播接收器，
        locationReceiver = new LocationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.broadcast_name);
        registerReceiver(locationReceiver, filter);

        mInfoTex= (TextView) findViewById(R.id.tv_info);
        mInfoTex.setText("11111111");

        // 通知栏监控器开关
        Button notificationMonitorOnBtn = (Button)findViewById(R.id.notification_monitor_on_btn);
        notificationMonitorOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (!isEnabled()) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "监控器开关已打开", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        Button notificationMonitorOffBtn = (Button)findViewById(R.id.notification_monitor_off_btn);
        notificationMonitorOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (isEnabled()) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "监控器开关已关闭", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

//
        listview = (ListView) findViewById(R.id.listview);
        //初始化数据
        for (int i = 0; i < 8; i++) {
            map = new HashMap<String, Object>();
            map.put("Id", "100"+i);
            map.put("Name","Name_"+i);
            list.add(0,map);
        }
        adapter = new MyAdapter(this, list);
        listview.setAdapter(adapter);
    }

    public static MainActivity  getInstace(){
        return ins;
    }

    public void updateTheTextView(final String t) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mInfoTex.setText(t);

                //只用这里改变了
                adapter.notifyDataSetChanged();

                Log.i("XSL_Test","updateTheTextView ");
            }
        });
    }


    // 判断是否打开了通知监听权限
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        String detag;
        // detag = String.format("MethodName:\t%s LineNumber:\t%s\n",CurrentLineInfo.getMethodName(),CurrentLineInfo.getLineNumber());//CurrentLineInfo.getFileName()+CurrentLineInfo.getClassName()+CurrentLineInfo.getMethodName()+CurrentLineInfo.getLineNumber(;
        //Log.i(,flat)
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
//        卸载广播接收器
        unregisterReceiver(locationReceiver);

        super.onDestroy();
    }

    //内部类，实现BroadcastReceiver
    public class LocationReceiver extends BroadcastReceiver {
        //必须要重载的方法，用来监听是否有广播发送
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            Log.i("XSL_Test","接收到广播 ");

            if (intentAction.equals(MainActivity.broadcast_name)) {
                Bundle bundle=intent.getExtras();
                String strResult = bundle.getString("amount");
                Log.i("XSL_Test","金额: "+ strResult);
                String strtitle = bundle.getString("title");
                Log.i("XSL_Test","title: "+ strtitle);
                String strcontent = bundle.getString("content");
                Log.i("XSL_Test","content: "+ strcontent);
                mInfoTex.setText(strResult);

                cal = Calendar.getInstance();
                cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

                year = String.valueOf(cal.get(Calendar.YEAR));
                month = String.valueOf(cal.get(Calendar.MONTH))+1;
                day = String.valueOf(cal.get(Calendar.DATE));
                if (cal.get(Calendar.AM_PM) == 0)
                    hour = String.valueOf(cal.get(Calendar.HOUR));
                else
                    hour = String.valueOf(cal.get(Calendar.HOUR)+12);
                minute = String.valueOf(cal.get(Calendar.MINUTE));
                second = String.valueOf(cal.get(Calendar.SECOND));

                my_time_1 = year + "-" + month + "-" + day;
                my_time_2 = hour + "-" + minute + "-" + second;

                try {
                    MainActivity.getInstace().updateTheTextView(my_time_2+",金额: "+ strResult+","+"title: "+ strtitle+","+"content: "+ strcontent);
                } catch (Exception e) {

                }
//                mInfoTex.setText(my_time_2+",金额: "+ strResult+","+"title: "+ strtitle+","+"content: "+ strcontent);

                map = new HashMap<String, Object>();
                map.put("Id", strtitle);
                map.put("Name", strcontent);
                list.add(0,map);


            }
        }
    }


    //自定义adapter
    public class MyAdapter extends BaseAdapter {
        List<Map<String, Object>>list;
        LayoutInflater inflater;
        public MyAdapter(Context context,List<Map<String, Object>>list){
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder viewHolder;
            if(convertView==null){
                convertView = inflater.inflate(R.layout.item, null);
                viewHolder = new ViewHolder();
                viewHolder.tv1 = (TextView) convertView.findViewById(R.id.tv1);
                viewHolder.tv2 =(TextView) convertView.findViewById(R.id.tv2);
                convertView.setTag(viewHolder);

            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tv1.setText(list.get(position).get("Id").toString());
            viewHolder.tv2.setText(list.get(position).get("Name").toString());
            return convertView;
        }

    }
    //辅助类
    class ViewHolder{
        TextView tv1;
        TextView tv2;
    }

}
