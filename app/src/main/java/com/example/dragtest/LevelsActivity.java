package com.example.dragtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class LevelsActivity extends AppCompatActivity {

    ArrayList<item> arrayList;
    RecyclerView recyclerView;
    String levelName[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    /*Todo: Make levelImage id array*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        recyclerView = findViewById(R.id.horizontalScrollView);
        arrayList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        for(int i = 0; i < levelName.length; i++){
            item item = new item();
            item.setImage(R.mipmap.ninja);
            item.setName(String.valueOf((i + 1)));
            Log.e("Error in Name: ", levelName[i]);
            arrayList.add(item);
        }
        CustomAdapter adapter = new CustomAdapter(getApplicationContext(), arrayList);
        recyclerView.setAdapter(adapter);
    }
}