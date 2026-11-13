package com.mlm.external;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import java.util.Calendar;
import java.util.Locale;

public class BalanceWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        MyDataStore dataManager = new MyDataStore(context);
        int balance = dataManager.totalMoneyNow();
        int todayExpense = dataManager.getTodayExpenseAmount();

        Calendar now = Calendar.getInstance();
        String allowanceCycle = dataManager.getAllowanceCycle();
        int daysLeft;

        if ("weekly".equals(allowanceCycle)) {
            int targetDayOfWeek = dataManager.getAllowanceDay();
            int currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK);
            int currentConverted = (currentDayOfWeek == Calendar.SUNDAY) ? 6 : (currentDayOfWeek - 2);
            int diffDays = targetDayOfWeek - currentConverted;
            if (diffDays < 0) diffDays += 7;
            daysLeft = diffDays;
        } else {
            int currentDayOfMonth = now.get(Calendar.DAY_OF_MONTH);
            int targetMonthlyDay = dataManager.getAllowanceDay() + 1;
            if (targetMonthlyDay >= currentDayOfMonth) {
                daysLeft = targetMonthlyDay - currentDayOfMonth;
            } else {
                Calendar nextMonth = (Calendar) now.clone();
                nextMonth.add(Calendar.MONTH, 1);
                int maxDay = nextMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
                int target = Math.min(targetMonthlyDay, maxDay);
                nextMonth.set(Calendar.DAY_OF_MONTH, target);
                long diff = nextMonth.getTimeInMillis() - now.getTimeInMillis();
                daysLeft = (int) (diff / (1000 * 60 * 60 * 24));
            }
        }
        if (daysLeft < 0) daysLeft = 0;

        int divisor = Math.max(1, daysLeft);
        int recommendedDaily = balance / divisor;

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_balance);
        views.setTextViewText(R.id.tvWidgetBalanceValue, String.format(Locale.getDefault(), "%,d원", balance));
        views.setTextViewText(R.id.tvWidgetTodayExpense, String.format(Locale.getDefault(), "오늘 지출: %,d원", todayExpense));
        views.setTextViewText(R.id.tvWidgetRecommendedDaily, String.format(Locale.getDefault(), "권장 지출액: %,d원 (D-%d)", recommendedDaily, daysLeft));

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widget_balance_root, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateAllWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, BalanceWidgetProvider.class);
        int[] ids = manager.getAppWidgetIds(componentName);
        if (ids != null && ids.length > 0) {
            for (int id : ids) {
                updateAppWidget(context, manager, id);
            }
        }
    }
}
