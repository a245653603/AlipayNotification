package com.alipay.wan.alipaynotification;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import java.text.Collator;
import java.util.Locale;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationMonitorService extends NotificationListenerService
{
    private static final String TAG = "NotificationMonitorService";
    public static final int NOTICE_ID = 100;

    public NotificationMonitorService() {
    }

    static Collator co = Collator.getInstance(Locale.CHINA);
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        // 如果Service被杀死，干掉通知
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
//            NotificationManager mManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//            mManager.cancel(NOTICE_ID);
//        }
////        if(Contants.DEBUG)
////            Log.d(TAG,"DaemonService---->onDestroy，前台service被杀死");
//        // 重启自己
//        Intent intent = new Intent(getApplicationContext(),NotificationMonitorService.class);
//        startService(intent);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
////        return super.onStartCommand(intent, flags, startId);
//        // 如果Service被终止
//        // 当资源允许情况下，重启service
//        return START_STICKY;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        //如果API大于18，需要弹出一个可见通知
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
//            Notification.Builder builder = new Notification.Builder(this);
//            builder.setSmallIcon(R.mipmap.ic_launcher);
//            builder.setContentTitle("KeepAppAlive");
//            builder.setContentText("DaemonService is runing...");
//            startForeground(NOTICE_ID,builder.build());
//            // 如果觉得常驻通知栏体验不好
//
//        }else{
//            startForeground(NOTICE_ID,new Notification());
//        }
//    }

    // 在收到消息时触发
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        Log.i("XSL_Test", "Notification posted " + notificationTitle + " & " + notificationText);

        //提取金额

        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]+|[\\d.]+");
        Matcher m = p.matcher( notificationText );
        Stack<String> st = new Stack<String>();
        while ( m.find() ) {
//            Log.i("XSL_Test11", "Notification posted " + m.group()  );
            st.push(m.group());
        }
        st.pop();
        String strAmount=st.pop();
        Log.i("XSL_Test11", "Notification posted " + strAmount );
        if(notificationTitle.indexOf("支付宝通知")>-1 || notificationTitle.indexOf("微信通知")>-1) {
            Log.i("XSL_Test", "Notification posted:支付宝通知 ");

            Intent intent = new Intent();
            intent.putExtra("amount", strAmount);
            intent.putExtra("title", notificationTitle);
            intent.putExtra("content", notificationText);
//
            String  broadcast_name=MainActivity.broadcast_name;//this.getResources().getString(R.string.broadcast_name);
            intent.setAction(broadcast_name);
            sendBroadcast(intent);

        }

        Toast.makeText(NotificationMonitorService.this, "show_content:" +
                        "Notification posted " + notificationTitle + " & " + notificationText
                , Toast.LENGTH_LONG).show();

    }

    // 在删除消息时触发
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        Log.i("XSL_Test", "Notification removed " + notificationTitle + " & " + notificationText);
        Toast.makeText(NotificationMonitorService.this, "show_content:" +
                        "Notification removed " + notificationTitle + " & " + notificationText
                , Toast.LENGTH_LONG).show();
    }
}
