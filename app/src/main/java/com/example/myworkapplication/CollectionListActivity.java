package com.example.myworkapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Button;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CollectionListActivity extends AppCompatActivity {

    private static final String TAG = "CollectionListActivity";
    private ListView listview;
    private CollectAdapter adapter;
    private List<CollectItem> collectList = new ArrayList<>();
    private CollectDBHelper dbHelper;
    private EditText editSearch;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_collection_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listview = findViewById(R.id.listView);
        dbHelper = new CollectDBHelper(this);

        collectList = dbHelper.getAllCollects();
        adapter = new CollectAdapter(this, collectList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener((parent, view, position, id) -> {
            CollectItem item = collectList.get(position);

//            Intent intent = new Intent(CollectionListActivity.this, EditCollectActivity.class);
//            intent.putExtra("id", item.getId());
//            intent.putExtra("title", item.getTitle());
//            intent.putExtra("content", item.getContent());
//
//            startActivityForResult(intent, 1);

             //弹窗实现修改
            TextView title = new TextView(CollectionListActivity.this);
            title.setText("修改收藏");
            title.setTextSize(20);
            title.setPadding(32, 32, 32, 32);
            title.setTypeface(null, Typeface.BOLD);
            title.setGravity(Gravity.CENTER);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(CollectionListActivity.this,R.style.PinkDialogButtonStyle);
            builder.setCustomTitle(title);

            LayoutInflater inflater = LayoutInflater.from(CollectionListActivity.this);
            View dialogView = inflater.inflate(R.layout.dialog_edit_collect, null);

            EditText titleEdit = dialogView.findViewById(R.id.edit_title);
            EditText contentEdit = dialogView.findViewById(R.id.edit_content);

            titleEdit.setText(item.getTitle().trim());
            contentEdit.setText(item.getContent());

            builder.setView(dialogView);

            builder.setPositiveButton("保存", (dialog, which) -> {
                item.setTitle(titleEdit.getText().toString());
                item.setContent(contentEdit.getText().toString());

                CollectDBHelper dbHelper = new CollectDBHelper(CollectionListActivity.this);
                dbHelper.updateCollect(item.getId(), item.getTitle(), item.getContent());
                loadCollects();
            });

            builder.setNegativeButton("取消", null);
            builder.show();
        });

        listview.setOnItemLongClickListener((parent, view, position, id) -> {
            CollectItem item = collectList.get(position);

            new AlertDialog.Builder(CollectionListActivity.this)
                    .setTitle(item.getTitle())
                    .setMessage("确定要删除此收藏项吗？")
                    .setPositiveButton("删除", (dialog, which) -> {
                        CollectDBHelper dbHelper = new CollectDBHelper(CollectionListActivity.this);
                        dbHelper.deleteCollect(item.getId());

                        collectList.remove(position);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(CollectionListActivity.this, "已删除收藏", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return true;
        });

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(CollectionListActivity.this, AddCollectActivity.class);
            startActivity(intent);
        });

        Button btnWebSearch = findViewById(R.id.btn_open_web_search);
        btnWebSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CollectionListActivity.this, webSearchActivity.class);
                startActivity(intent);
            }
        });


        editSearch = findViewById(R.id.editSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            String keyword = editSearch.getText().toString().trim();
            if (keyword.isEmpty()){
                Toast.makeText(this,"请输入关键词",Toast.LENGTH_SHORT).show();
                return;
            }
            dbHelper = new CollectDBHelper(this);
            List<CollectItem> allItems = dbHelper.getAllCollects();
            List<CollectItem> filteredItems = new ArrayList<>();

            for (CollectItem item : allItems) {
                if (item.getTitle().contains(keyword) || item.getContent().contains(keyword) ||
                        item.getContent().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredItems.add(item);
                }
            }
            collectList.clear();
            collectList.addAll(filteredItems);
            adapter.notifyDataSetChanged();

        });
    }

    private void loadCollects() {
        CollectDBHelper dbHelper = new CollectDBHelper(this);
        List<CollectItem> newItems = dbHelper.getAllCollects();
        Log.i(TAG, "loadCollects: 数据库收藏数" + newItems.size());
        collectList.clear(); // 清空当前列表
        collectList.addAll(newItems);
        adapter.notifyDataSetChanged();

        loadHealthTipsFromWeb();
    }

    private void loadHealthTipsFromWeb() {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect("https://www.sohu.com/a/168688898_464393").get();
                Elements items = doc.select(".new_list li");

                ArrayList<CollectItem> tips = new ArrayList<>();
                for (Element item : items) {
                    Element link = item.selectFirst("a");
                    if (link != null) {
                        String title = link.text();
                        tips.add(new CollectItem(-1, title,"","1"));
                    }
                }

                runOnUiThread(() -> {
                    collectList.addAll(tips);
                    adapter.notifyDataSetChanged();
                });

            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(CollectionListActivity.this, "加载健康提示失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

//        private void filterCollects(String keyword) {
//            if (TextUtils.isEmpty(keyword)) {
//                loadCollects(); // 如果没有输入关键字，重新加载所有数据
//                return;
//            }
//
//            List<CollectItem> filteredList = new ArrayList<>();
//            for (CollectItem item : collectItemList) {
//                if (item.getTitle().contains(keyword) ||
//                    item.getContent().contains(keyword)) {
//                    filteredList.add(item);
//                }
//            }
//            adapter.setData(filteredList);
//            adapter.notifyDataSetChanged();
//        }

    @Override
    protected void onResume() {
        super.onResume();
        loadCollects();
        editSearch.setText("");
    }

}