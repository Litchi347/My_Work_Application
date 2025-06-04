package com.example.myworkapplication;

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

public class DiseaseSearchActivity extends AppCompatActivity {

    private EditText editDisease;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_disease_search);

        editDisease = findViewById(R.id.editDisease);
        btnSearch = findViewById(R.id.btnSearch);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSearch.setOnClickListener(v -> {
            String keyword = editDisease.getText().toString();
            if (!keyword.isEmpty()) {
                Intent intent = new Intent(DiseaseSearchActivity.this, SearchResultActivity.class);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
            } else {
                Toast.makeText(this, "请输入疾病名称", Toast.LENGTH_SHORT).show();
            }
        });
    }
}