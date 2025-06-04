package com.example.myworkapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SearchResultActivity extends AppCompatActivity {
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String keyword = getIntent().getStringExtra("keyword");

        if(keyword != null) {
            new Thread(() -> {
                try {
                    String url = "https://jbk.39.net/ "+ keyword + "/";
                    Document doc = Jsoup.connect(url).get();
                    Elements info = doc.select("div.info");
                    String result = info.text();
                    runOnUiThread(() -> resultView.setText(result.isEmpty() ? "未找到相关信息" : result));
                } catch (Exception e) {
                    runOnUiThread(() ->  resultView.setText("抓取失败: " + e.getMessage()));
                }
            }).start();
        }
    }
}