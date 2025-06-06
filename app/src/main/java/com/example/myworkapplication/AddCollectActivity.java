package com.example.myworkapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddCollectActivity extends AppCompatActivity {
    private EditText editTitle;
    private EditText editContent;
    private Button btnSave;
    private CollectDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_collect);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new CollectDBHelper(this);

        btnSave.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String content = editContent.getText().toString().trim();

            if (!title.isEmpty() && !content.isEmpty()) {
                dbHelper.addCollect(title, content, "manual");
                Toast.makeText(AddCollectActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddCollectActivity.this, "标题和内容不能为空", Toast.LENGTH_SHORT).show();
            }
        });
    }
}