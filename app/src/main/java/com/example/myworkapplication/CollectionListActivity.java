package com.example.myworkapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class CollectionListActivity extends AppCompatActivity {

    private ListView listview;
    private CollectAdapter adapter;
    private ArrayList<Collect> dataList;
    private CollectDao dao;

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

        listview = findViewById(R.id.listview);
        dao = new CollectDao(this);

        loadData();

        listview.setOnItemClickListener((parent, view, position, id) -> {
            CollectItem item = dataList.get(position);
            Intent intent = new Intent(CollectionListActivity.this, EditItemActivity.class);
            startActivity(intent);
        });

        listview.setOnItemLongClickListener((parent, view, position, id) -> {
            CollectItem item = dataList.get(position);
            dao.delete(item.getId());
            Toast.makeText(this, "已删除" + item.getTitle(), Toast.LENGTH_SHORT).show();
            loadData();
            return true;
        });
    }

    private void loadData() {
        dataList = dao.getAll();
        if (adapter == null) {
            adapter = new CollectAdapter(this, dataList);
            listview.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(dataList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();    // 回到列表时刷新数据
    }
}