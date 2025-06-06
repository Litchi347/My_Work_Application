package com.example.myworkapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


public class webSearchActivity extends AppCompatActivity {
    private EditText searchInput;
    private Button searchButton;
    private TextView searchResult;

    private static final String TARGET_URL = "https://www.sohu.com/a/168688898_464393";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_web_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchInput = findViewById(R.id.search_input);
        searchButton = findViewById(R.id.search_button);
        searchResult = findViewById(R.id.search_result);

        searchButton.setOnClickListener(v -> {
            String keyword = searchInput.getText().toString();

            if (!keyword.isEmpty()) {
                new SearchTask().execute(keyword);
            } else {
                searchResult.setText("请输入搜索关键词");
            }
        });

        searchResult.setOnClickListener(v -> {
            String title = searchInput.getText().toString();
            String content = searchResult.getText().toString();

            new AlertDialog.Builder(webSearchActivity.this)
                    .setTitle("添加收藏")
                    .setMessage("是否将这段内容加入收藏？")
                    .setPositiveButton("确定", (dialog, which) -> {
                        CollectDBHelper dbHelper = new CollectDBHelper(webSearchActivity.this);
                        dbHelper.addCollect(title.isEmpty() ? "网页收藏" : title, content, "网页");
                        Toast.makeText(webSearchActivity.this, "收藏成功!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(webSearchActivity.this, CollectionListActivity.class));
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }
    private class SearchTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            searchResult.setText("正在搜索...");
        }

        @Override
        protected String doInBackground(String... params) {
            String keyword = params[0];
            try {
                Document doc = Jsoup.connect(TARGET_URL).get();

                String text = doc.body().text();
                if(text.contains(keyword)) {
                    int start = text.indexOf(keyword);
                    int end = Math.min(start + 100, text.length());
                    return "找到内容：\n\n" + text.substring(start, end) + "...";
                } else {
                    return "未找到相关内容";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "抓取失败：" + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            searchResult.setText(result);
        }
    }
}