package com.develiark.android_park;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import ru.mail.weather.lib.Storage;
import ru.mail.weather.lib.Topics;

public class SecondActivity extends AppCompatActivity {
    private Storage storage;
    private String[] categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        setTitle(getResources().getString(R.string.category_activity));

        storage = Storage.getInstance(getApplicationContext());
        categories = Topics.ALL_TOPICS;
        ListView lvCategories = (ListView) findViewById(R.id.lv_categories);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, categories);

        lvCategories.setAdapter(adapter);
        lvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position >= 0 && position < categories.length)
                    closeActivity(categories[position]);
                else
                    closeActivity(Topics.AUTO);
            }
        });
    }

    private void closeActivity(String categoryName) {
        storage.saveCurrentTopic(categoryName);
        finish();
    }
}
