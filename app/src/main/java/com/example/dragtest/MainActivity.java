package com.example.dragtest;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity {

    ImageView player;
    GridLayout gridLayout;
    public int position , row, column;
    public int movement = 0 ;
    public float rotate = 0;
    public int[] size = new int[100];
    public boolean flag = false, LeftBound = false, RightBound = false, UpBound = false, DownBound = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String json = loadJSONFromAsset();

        try{

            JSONObject object = new JSONObject(loadJSONFromAsset());
            JSONArray jsonArray = object.getJSONArray("level");
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            JSONObject grid = jsonObject.getJSONObject("grid");

            row = grid.getInt("rows");
            column = grid.getInt("cols");

            JSONArray path = jsonObject.getJSONArray("path");
            int startIndex = jsonObject.getInt("start");

            position = startIndex;

            gridLayout = (GridLayout) findViewById(R.id.gridLayout);
            gridLayout.setColumnCount(column);
            gridLayout.setRowCount(row);

            int i = 1, pathIndex = 0, sizeIndex = 0;

            for(int r = 1; r <= row; r++){
                for(int c = 1; c <= column; c++ ) {

                    ImageView imageView = new ImageView(this);
                    JSONObject pathId = path.getJSONObject(pathIndex);
                    int id = pathId.getInt("id");
                    size[sizeIndex++] = id;
                    if(pathIndex < 25){
                        if(id == i){
                            imageView.setImageResource(R.mipmap.tile);
                            pathIndex++;
                        }
                        else imageView.setImageResource(R.mipmap.crate);

                    }


                    if(pathIndex == startIndex){
                        imageView.setImageResource(R.mipmap.player_idle);
                    }

                    imageView.setId(i++);
                    GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
                    GridLayout.Spec colSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);

                    if (r == 0 && c == 0) {
                        Log.e("", "specs");
                        rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 2);
                        colSpan = GridLayout.spec(GridLayout.UNDEFINED, 2);
                    }
                    GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams(rowSpan, colSpan);
                    gridLayout.addView(imageView, gridParams);
                }
            }

            }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Button right = findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position % column != 0 || LeftBound == true ){
                    player = (ImageView) findViewById(position);
                    for(int i = 0; i < size.length ; i++){
                        if(size[i] == (position + 1)){
                            flag = true;
                            break;
                        }
                    }

                    if(flag){
                        player.setImageResource(R.mipmap.tile);
                        position++;
                        ImageView newPosition = (ImageView) findViewById(position);
                        newPosition.setImageResource(R.mipmap.player_idle);
                        flag = false;
                    }
                    else {
                        player.setImageResource(R.mipmap.tile);
                        position++;
                        ImageView newPosition = (ImageView) findViewById(position);
                        newPosition.setImageResource(R.mipmap.player_dead);
                        Toast.makeText(getApplicationContext(), "Lost", Toast.LENGTH_SHORT).show();
                    }
                    LeftBound = false;
                }
                else{
                    Toast.makeText(getApplicationContext(), "Out of Bounds", Toast.LENGTH_SHORT).show();
//                    position--;
                    LeftBound = true;
                }

                Log.e("Position: ", Integer.toString(position));
            }
        });

        Button left = findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((position % column != 0 && (position - 1) != 0) || RightBound == true){
                    player = (ImageView) findViewById(position);
                    for(int i = 0; i < size.length ; i++){
                        if(size[i] == (position - 1)){
                            flag = true;
                            break;
                        }
                    }

                    if(flag){
                        player.setImageResource(R.mipmap.tile);
                        position--;
                        ImageView newPosition = (ImageView) findViewById(position);
                        newPosition.setImageResource(R.mipmap.player_idle);
                        flag = false;
                    }
                    else {
                        player.setImageResource(R.mipmap.tile);
                        position--;
                        ImageView newPosition = (ImageView) findViewById(position);
                        newPosition.setImageResource(R.mipmap.player_dead);
                        Toast.makeText(getApplicationContext(), "Lost", Toast.LENGTH_SHORT).show();
                    }
                    RightBound = false;
                }
                else{
                    Toast.makeText(getApplicationContext(), "Out of Bounds", Toast.LENGTH_SHORT).show();
//                    position++;
                    RightBound = true;
                }
                Log.e("Position: ", Integer.toString(position));

            }
        });

    }

    public void makeLayout(){
        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        gridLayout.setColumnCount(column);
        gridLayout.setRowCount(row);
        int i = 1;
        for(int r = 1; r <= row; r++){
            for(int c = 1; c <= column; c++ ) {

                ImageView imageView = new ImageView(this);
                if( r == c) imageView.setImageResource(R.mipmap.crate);
                else imageView.setImageResource(R.mipmap.tile);

                if(r == 1 && c == 1){
                    imageView.setImageResource(R.mipmap.player_idle);
                    ImageView player = new ImageView(this);

                    player.setImageResource(R.mipmap.tile);
                }

                imageView.setId(i++);
                GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
                GridLayout.Spec colSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);

                if (r == 0 && c == 0) {
                    Log.e("", "specs");
                    rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 2);
                    colSpan = GridLayout.spec(GridLayout.UNDEFINED, 2);
                }
                GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams(rowSpan, colSpan);
                gridLayout.addView(imageView, gridParams);
            }
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("levels.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}