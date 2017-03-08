package com.develiark.android_park;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Locale;

import ru.mail.weather.lib.News;
import ru.mail.weather.lib.Storage;

public class MainActivity extends AppCompatActivity implements ServiceHelper.NewsResultListener {

    private Storage storage;
    private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd-MM-yy, HH:mm:ss", Locale.US);
    private final static String CATEGORY = "category";
    private TextView txtTitle, txtDate, txtBody;
    private ActionBar actionBar;
    private String appName;

    static {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .penaltyLog()
                .penaltyDeath()
                .build()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = Storage.getInstance(getApplicationContext());
        appName = getResources().getString(R.string.app_name);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
            actionBar.setTitle(appName + " - " + getCategory());
            invalidateOptionsMenu();
        }

        txtTitle = (TextView) findViewById(R.id.text_title);
        txtBody = (TextView) findViewById(R.id.text_body);
        txtDate = (TextView) findViewById(R.id.text_date);
        Button btnCity = (Button) findViewById(R.id.btn_category);
        Button btnStartUpdate = (Button) findViewById(R.id.btn_update_yes);
        Button btnStopUpdate = (Button) findViewById(R.id.btn_update_no);

        btnCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNews(false);
            }
        });

        btnStartUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNews(true);
            }
        });

        btnStopUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopBackgroundUpdate();
            }
        });
    }

    private void getNews(boolean background) {
        ServiceHelper.getInstance(this).getNews(this, getCategory(), this, background);
    }

    private void stopBackgroundUpdate() {
        ServiceHelper.getInstance(this).stopNewsBackground(this);
    }

    private String getCategory() {
        String category = storage.loadCurrentTopic();
        String[] categories = getResources().getStringArray(R.array.array_categories);

        if (!category.isEmpty())
            return category;
        else if (categories.length > 0)
            return categories[0];

        return CATEGORY;
    }

    private void updateNews() {
        try {
            News news = storage.getLastSavedNews();
            txtTitle.setText(String.format("%s", news.getTitle()));
            txtDate.setText(String.format("%s", FORMAT.format(news.getDate())));
            txtBody.setText(String.format("%s", news.getBody()));
        } catch (NullPointerException n) {
            System.out.println(n.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SecondActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNews(false);
        updateNews();
        actionBar.setTitle(appName + " - " + getCategory());
    }

    @Override
    protected void onStop() {
        ServiceHelper.getInstance(this).removeListener();
        super.onStop();
    }

    @Override
    public void onNewsResult(final boolean success) {
        if (success)
            updateNews();
        else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.info),
                            Toast.LENGTH_SHORT).show();
    }
}