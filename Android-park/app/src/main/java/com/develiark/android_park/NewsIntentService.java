package com.develiark.android_park;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

import ru.mail.weather.lib.News;
import ru.mail.weather.lib.NewsLoader;
import ru.mail.weather.lib.Storage;

public class NewsIntentService extends IntentService {
    public final static String EXTRA_NEWS_CATEGORY = "extra.NEWS_CATEGORY";
    public final static String ACTION_NEWS_RESULT_SUCCESS = "action.ACTION_NEWS_RESULT_SUCCESS";
    public final static String ACTION_NEWS_RESULT_ERROR = "action.ACTION_NEWS_RESULT_ERROR";

    public NewsIntentService() {
        super("NewsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String text = intent.getStringExtra(EXTRA_NEWS_CATEGORY);
        boolean success;

        try {
            NewsLoader loader = new NewsLoader();
            Storage storage = Storage.getInstance(getApplicationContext());
            News news = loader.loadNews(text);
            storage.saveNews(news);
            success = true;
        } catch (IOException ex) {
            success = false;
        }

        final Intent intentBroadcast = new Intent(success ? ACTION_NEWS_RESULT_SUCCESS :
                                                            ACTION_NEWS_RESULT_ERROR);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentBroadcast);
    }
}