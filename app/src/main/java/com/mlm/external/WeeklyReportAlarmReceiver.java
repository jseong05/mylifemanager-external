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
import java.util.List;
import java.util.Locale;

/**
 * 매주 일요일 저녁 8시 주간 지출 및 낭비 결산 리포트 알림
 */
public class WeeklyReportAlarmReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "mlm_weekly_report_channel";

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        MyDataStore dataManager = new MyDataStore(context);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "주간 결산 리포트 채널",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("매주 일요일 저녁 한 주간의 지출 및 낭비 리포트 알림");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                2003,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        List<Expense> expenses = dataManager.getExpenses();
        int totalExpense = 0;
        int wasteCount = 0;
        for (Expense e : expenses) {
            if (e.isMinus()) {
                totalExpense += e.getMoney();
                if (e.isWaste()) {
                    wasteCount++;
                }
            }
        }

        String message = String.format(Locale.getDefault(), "📊 이번 주 주간 결산 리포트가 도착했어요!\n총 지출: %,d원 | 낭비 지출: %d건 감지됨",
                totalExpense, wasteCount);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setContentTitle("마이 라이프 메니저 주간 리포트")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(3003, builder.build());
        }
    }
}
