package com.develiark.android_park;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import ru.mail.weather.lib.Scheduler;

class ServiceHelper {
    private NewsResultListener newsListener;
    private static ServiceHelper instance;
    private boolean backgroundOnline = false;
    private Intent backgroundIntent;
    private Scheduler scheduler = Scheduler.getInstance();

    private ServiceHelper() {
    }

    synchronized static ServiceHelper getInstance(final Context context) {
        if (instance == null) {
            instance = new ServiceHelper();
            instance.initBroadcastReceiver(context);
        }
        return instance;
    }

    private void initBroadcastReceiver(Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(NewsIntentService.ACTION_NEWS_RESULT_SUCCESS);
        filter.addAction(NewsIntentService.ACTION_NEWS_RESULT_ERROR);

        LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                if (newsListener != null) {
                    final boolean success = intent.getAction().equals(NewsIntentService.ACTION_NEWS_RESULT_SUCCESS);
                    newsListener.onNewsResult(success);
                }
            }
        }, filter);
    }

    void getNews(final Context context, final String category, final NewsResultListener listener,
                final boolean isBackground) {
        newsListener = listener;

        Intent intent = new Intent(context, NewsIntentService.class);
        intent.putExtra(NewsIntentService.EXTRA_NEWS_CATEGORY, category);

        if (isBackground) {
            backgroundOnline = true;
            backgroundIntent = intent;
            scheduler.schedule(context, intent, 3000);
            System.out.println("scheduler");
        } else
            context.startService(intent);
    }

    void stopNewsBackground(final Context context) {
        if (backgroundOnline && backgroundIntent != null) {
            backgroundOnline = false;
            scheduler.unschedule(context, backgroundIntent);
        }
    }

    void removeListener() {
        newsListener = null;
    }

    interface NewsResultListener {
        void onNewsResult(final boolean success);
    }
}