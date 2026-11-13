package com.mlm.external;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 매일 아침 08:00 모닝 브리핑 알림 (오늘 일정 개수 및 권장 지출 안내)
 */
public class MorningBriefingAlarmReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "mlm_morning_briefing_channel";

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        MyDataStore dataManager = new MyDataStore(context);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "모닝 브리핑 채널",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("매일 아침 오늘의 일정과 자산 상태를 알려주는 브리핑 알림");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                2002,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        List<Schedule> allSchedules = dataManager.getSchedules();
        int scheduleCount = 0;
        for (Schedule s : allSchedules) {
            if (s.getDate() != null && s.getDate().equals(today)) {
                scheduleCount++;
            }
        }

        int balance = dataManager.totalMoneyNow();
        String message = String.format(Locale.getDefault(), "☀️ 굿모닝 %s님! 오늘 예정된 일정이 %d개 있어요. 현재 잔액: %,d원",
                dataManager.getUserName(), scheduleCount, balance);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_agenda)
                .setContentTitle("마이 라이프 메니저 모닝 브리핑")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(3002, builder.build());
        }
    }
}
