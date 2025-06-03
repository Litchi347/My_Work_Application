package com.example.myworkapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class CollectionListActivity extends AppCompatActivity {

    private ListView listview;
    private CollectAdapter adapter;
    private ArrayList<CollectItem> collectItemList;

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

            AlertDialog.Builder builder = new AlertDialog.Builder(CollectionListActivity.this);
            builder.setTitle("修改");

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            final EditText titleEdit = new EditText(this);
            titleEdit.setText(item.getTitle());
            layout.addView(titleEdit);

            final EditText contentEdit = new EditText(this);
            contentEdit.setText(item.getContent());
            layout.addView(contentEdit);

            builder.setView(layout);

            builder.setPositiveButton("保存", (dialog, which) -> {

                item.setTitle(titleEdit.getText().toString());
                item.setContent(contentEdit.getText().toString());

                CollectDBHelper dbHelper = new CollectDBHelper(CollectionListActivity.this);
                dbHelper.updateCollect(item);
                loadCollects();
            });
            builder.setNegativeButton("取消", null);
            builder.show();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCollects(); // 每次返回时重新加载收藏列表
    }

    private void loadCollects() {
        CollectDBHelper dbHelper = new CollectDBHelper(this);
        List<CollectItem> newItems = dbHelper.getAllCollects();
        collectItemList.clear(); // 清空当前列表
        collectItemList.addAll(newItems);
        adapter.notifyDataSetChanged();
    }

}