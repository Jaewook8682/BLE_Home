package com.setting.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.setting.myapplication.adapter.DataAdapter;

import java.util.ArrayList;

public class DataActivity extends AppCompatActivity {

    RecyclerView recycler;
    ArrayList<SaveData> alSave;
    DataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        alSave = (ArrayList<SaveData>) getIntent().getSerializableExtra("saveList");

        initList();
    }

    private void initList() {

            recycler = findViewById(R.id.recycler);

            adapter = new DataAdapter(this, alSave);
            recycler.setLayoutManager(new LinearLayoutManager(DataActivity.this));
            recycler.setAdapter(adapter);

            DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            recycler.addItemDecoration(decoration);


    }
}