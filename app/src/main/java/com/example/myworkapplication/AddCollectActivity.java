package com.example.myworkapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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
            String title = editTitle.getText().toString();
            String content = editContent.getText().toString();

            if (!title.isEmpty() && !content.isEmpty()) {
                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("content", content);

                dbHelper.getWritableDatabase().insert("collect", null, values);

                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}