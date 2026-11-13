package com.mlm.external;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        MyDataStore dataManager = new MyDataStore(context);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());

        List<Schedule> allSchedules = dataManager.getSchedules();
        List<Schedule> todaySchedules = new ArrayList<>();
        for (Schedule s : allSchedules) {
            if (isScheduleOnDate(s, today)) {
                todaySchedules.add(s);
            }
        }

        StringBuilder content = new StringBuilder();
        if (todaySchedules.isEmpty()) {
            content.append("오늘 등록된 일정이 없습니다. 🎉");
        } else {
            int maxShow = Math.min(4, todaySchedules.size());
            for (int i = 0; i < maxShow; i++) {
                Schedule s = todaySchedules.get(i);
                String check = s.isCompleted() ? "☑️ " : "▫️ ";
                content.append(check).append("[").append(s.getWhen()).append("] ").append(s.getTodo());
                if (i < maxShow - 1) content.append("\n");
            }
            if (todaySchedules.size() > 4) {
                content.append("\n외 ").append(todaySchedules.size() - 4).append("개 일정 더 있음");
            }
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_schedule);
        views.setTextViewText(R.id.tvWidgetScheduleContent, content.toString());

        Intent intent = new Intent(context, ScheduleActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widget_schedule_root, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static boolean isScheduleOnDate(Schedule s, String targetDateText) {
        if (s == null || targetDateText == null || s.getDate() == null || s.getDate().isEmpty()) {
            return false;
        }

        String startDateText = s.getDate();
        if (startDateText.compareTo(targetDateText) > 0) {
            return false;
        }

        String repeat = s.getRepeatOption();
        if ("NONE".equalsIgnoreCase(repeat)) {
            return startDateText.equals(targetDateText);
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startDate = sdf.parse(startDateText);
            Date targetDate = sdf.parse(targetDateText);

            if (startDate == null || targetDate == null) return false;

            Calendar startCal = Calendar.getInstance();
            startCal.setTime(startDate);

            Calendar targetCal = Calendar.getInstance();
            targetCal.setTime(targetDate);

            if ("WEEKLY".equalsIgnoreCase(repeat)) {
                return startCal.get(Calendar.DAY_OF_WEEK) == targetCal.get(Calendar.DAY_OF_WEEK);
            } else if ("MONTHLY".equalsIgnoreCase(repeat)) {
                return startCal.get(Calendar.DAY_OF_MONTH) == targetCal.get(Calendar.DAY_OF_MONTH);
            } else if ("YEARLY".equalsIgnoreCase(repeat)) {
                return (startCal.get(Calendar.MONTH) == targetCal.get(Calendar.MONTH)) &&
                       (startCal.get(Calendar.DAY_OF_MONTH) == targetCal.get(Calendar.DAY_OF_MONTH));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return startDateText.equals(targetDateText);
    }

    public static void updateAllWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, ScheduleWidgetProvider.class);
        int[] ids = manager.getAppWidgetIds(componentName);
        if (ids != null && ids.length > 0) {
            for (int id : ids) {
                updateAppWidget(context, manager, id);
            }
        }
    }
}
