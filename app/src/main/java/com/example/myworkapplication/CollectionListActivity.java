package com.example.myworkapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

//        collectItemList.add(new CollectItem("健康知识1", "关于饮食的建议"));
//        collectItemList.add(new CollectItem("健康知识2", "关于运动的建议"));
//        collectItemList.add(new CollectItem("健康知识3", "关于作息的建议"));

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
            CollectItem selectedItem = collectItemList.get(position);
            new AlertDialog.Builder(CollectionListActivity.this)
                    .setTitle(selectedItem.getTitle())
                    .setMessage("确定要删除此收藏项吗？")
                    .setPositiveButton("删除", (dialog, which) -> {
                        CollectDBHelper dbHelper = new CollectDBHelper(CollectionListActivity.this);
                        dbHelper.deleteCollect(selectedItem.getId());

                        collectItemList.remove(position);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(CollectionListActivity.this, "已删除收藏", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            loadCollects();
//        }
//    }

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