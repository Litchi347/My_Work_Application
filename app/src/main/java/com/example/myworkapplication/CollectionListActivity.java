package com.example.myworkapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    private ArrayList<CollectItem> collectItemList;
    private CollectDBHelper dbHelper;

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
        collectItemList = new ArrayList<>();
        adapter = new CollectAdapter(this, collectItemList);
        listview.setAdapter(adapter);

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(CollectionListActivity.this, AddCollectActivity.class);
            startActivity(intent);
        });

        listview.setOnItemClickListener((parent, view, position, id) -> {
            CollectItem item = collectItemList.get(position);

            Intent intent = new Intent(CollectionListActivity.this, EditCollectActivity.class);
            intent.putExtra("id", item.getId());
            intent.putExtra("title", item.getTitle());
            intent.putExtra("content", item.getContent());

            startActivityForResult(intent, 1);

//             //弹窗实现修改
//            AlertDialog.Builder builder = new AlertDialog.Builder(CollectionListActivity.this);
//            builder.setTitle("修改");
//
//            LinearLayout layout = new LinearLayout(this);
//            layout.setOrientation(LinearLayout.VERTICAL);
//            final EditText titleEdit = new EditText(this);
//            titleEdit.setText(item.getTitle());
//            layout.addView(titleEdit);
//
//            final EditText contentEdit = new EditText(this);
//            contentEdit.setText(item.getContent());
//            layout.addView(contentEdit);
//
//            builder.setView(layout);
//
//            builder.setPositiveButton("保存", (dialog, which) -> {
//
//                item.setTitle(titleEdit.getText().toString());
//                item.setContent(contentEdit.getText().toString());
//
//                CollectDBHelper dbHelper = new CollectDBHelper(CollectionListActivity.this);
//                dbHelper.updateCollect(item);
//                loadCollects();
//            });
//            builder.setNegativeButton("取消", null);
//            builder.show();
        });

        listview.setOnItemLongClickListener((parent, view, position, id) -> {
            CollectItem item = collectItemList.get(position);

            new AlertDialog.Builder(CollectionListActivity.this)
                    .setTitle(item.getTitle())
                    .setMessage("确定要删除此收藏项吗？")
                    .setPositiveButton("删除", (dialog, which) -> {
                        CollectDBHelper dbHelper = new CollectDBHelper(CollectionListActivity.this);
                        dbHelper.deleteCollect(item.getId());

                        collectItemList.remove(position);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(CollectionListActivity.this, "已删除收藏", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("取消", null)
                    .show();
            return true;
        });

        EditText editSearch = findViewById(R.id.editSearch);
        Button btnSearch = findViewById(R.id.btnSearch);

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

            collectItemList.clear();
            collectItemList.addAll(filteredItems);
            adapter.notifyDataSetChanged();

        });
    }

    private void loadCollects() {
        CollectDBHelper dbHelper = new CollectDBHelper(this);
        List<CollectItem> newItems = dbHelper.getAllCollects();
        collectItemList.clear(); // 清空当前列表
        collectItemList.addAll(newItems);
        adapter.notifyDataSetChanged();
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
        loadCollects(); // 每次返回时重新加载收藏列表

        EditText editSearch = findViewById(R.id.editSearch);
        editSearch.setText(""); // 清空搜索框
    }

}