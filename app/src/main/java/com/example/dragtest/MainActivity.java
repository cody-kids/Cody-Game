package com.example.dragtest;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    ImageView upArrow, downArrow, leftArrow, rightArrow, player;
    Button startButton;
    LinearLayout dropLayout;
    GridLayout gridLayout;
    boolean flag = true;
    public int position , row, column, end, pathIndex = 0;
    public int[] size = new int[100];
    public int[] path = new int[100];

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        upArrow = (ImageView) findViewById(R.id.upDirection);
        downArrow = (ImageView) findViewById(R.id.downDirection);
        leftArrow = (ImageView) findViewById(R.id.leftDirection);
        rightArrow = (ImageView) findViewById(R.id.rightDirection);
        startButton = (Button) findViewById(R.id.startButton);

        dropLayout = (LinearLayout) findViewById(R.id.dropLayout);

        upArrow.setOnLongClickListener(longClickListener);
        downArrow.setOnLongClickListener(longClickListener);
        leftArrow.setOnLongClickListener(longClickListener);
        rightArrow.setOnLongClickListener(longClickListener);

        dropLayout.setOnDragListener(dragListener);

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
            int endIndex = jsonObject.getInt("end");

            position = startIndex;
            end = endIndex;
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

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < path.length ; i++){


                    switch (path[i]){
                        case 1:
                            goUp();
                            break;
                        case -1:
                            goDown();
                            break;
                        case -2:
                            goLeft();
                            break;
                        case 2:
                            goRight();
                            break;
                    }

                    lagMove thread = new lagMove();
                    thread.run();

                }
            }
        });
    }

    void goUp(){
        boolean check = true;

                for(int i = 1; i <= column; i++){
                    if(position == i){
                        check = false;
                        break;
                    }
                }
                if(check){
                    player = (ImageView) findViewById(position);
                    for(int i = 0; i < size.length ; i++){
                        if(size[i] == (position - 5)){
                            flag = true;
                            break;
                        }
                    }

                    if(flag){
                        player.setImageResource(R.mipmap.tile);
                        position = position - 5;
                        ImageView newPosition = (ImageView) findViewById(position);
                        newPosition.setImageResource(R.mipmap.player_idle);
                        flag = false;
                    }
                    else {
                        player.setImageResource(R.mipmap.tile);
                        position= position -5;
                        if( position == end){
                            Toast.makeText(getApplicationContext(), "Won", Toast.LENGTH_SHORT).show();

                        }
                        ImageView newPosition = (ImageView) findViewById(position);
                        newPosition.setImageResource(R.mipmap.player_dead);
                        Toast.makeText(getApplicationContext(), "Lost", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Out of Bounds", Toast.LENGTH_SHORT).show();
                }
                Log.e("Position: ", Integer.toString(position));
    }

    void goDown(){
        boolean check = true;

        for(int i = (column*row - (column - 1)); i <= column * row; i++){
            if(position == i){
                check = false;
                break;
            }
        }

        if(check){
            player = (ImageView) findViewById(position);
            for(int i = 0; i < size.length ; i++){
                if(size[i] == (position + 5)){
                    flag = true;
                    break;
                }
            }

            if(flag){
                player.setImageResource(R.mipmap.tile);
                position = position + 5;
                if( position == end){
                    Toast.makeText(getApplicationContext(), "Won", Toast.LENGTH_SHORT).show();
                }

                ImageView newPosition = (ImageView) findViewById(position);
                newPosition.setImageResource(R.mipmap.player_idle);
                flag = false;
            }
            else {
                player.setImageResource(R.mipmap.tile);
                position = position + 5;
                ImageView newPosition = (ImageView) findViewById(position);
                newPosition.setImageResource(R.mipmap.player_dead);
                Toast.makeText(getApplicationContext(), "Lost", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Out of Bounds", Toast.LENGTH_SHORT).show();
        }
        Log.e("Position: ", Integer.toString(position));
    }

    void goRight(){
        if((position) % column != 0){
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

                if( position == end){
                    Toast.makeText(getApplicationContext(), "Won", Toast.LENGTH_SHORT).show();

                }
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
        }
        else{
            Toast.makeText(getApplicationContext(), "Out of Bounds", Toast.LENGTH_SHORT).show();
        }
        Log.e("Position: ", Integer.toString(position));
    }

    void goLeft(){
        if((position - 1) % column != 0 && (position - 1) != 0){
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
                if( position == end){
                    Toast.makeText(getApplicationContext(), "Won", Toast.LENGTH_SHORT).show();
                }
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
        }
        else{
            Toast.makeText(getApplicationContext(), "Out of Bounds", Toast.LENGTH_SHORT).show();
        }
        Log.e("Position: ", Integer.toString(position));
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



    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            return true;
        }
    };

    View.OnDragListener dragListener = new View.OnDragListener() {

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {

            int event = dragEvent.getAction();

            switch (event){
                case DragEvent.ACTION_DRAG_ENTERED :
                    final View v = (View) dragEvent.getLocalState();
                    if(v.getId() == R.id.upDirection){
                        ImageView image = new ImageView(getApplicationContext());
                        image.setImageResource(R.mipmap.up_arrow);
                        dropLayout.addView(image);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
                        image.setPadding(16,0 ,0 ,0);
                        image.setLayoutParams(params);
                        path[pathIndex++] = 1;
                    }

                    else if(v.getId() == R.id.downDirection){
                        ImageView image = new ImageView(getApplicationContext());
                        image.setImageResource(R.mipmap.down_arrow);
                        dropLayout.addView(image);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
                        image.setPadding(16,0 ,0 ,0);
                        image.setLayoutParams(params);
                        path[pathIndex++] = -1;

                    }

                    else if(v.getId() == R.id.leftDirection){
                        ImageView image = new ImageView(getApplicationContext());
                        image.setImageResource(R.mipmap.left_arrow);
                        dropLayout.addView(image);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
                        image.setPadding(16,0 ,0 ,0);
                        image.setLayoutParams(params);
                        path[pathIndex++] = -2;

                    }

                    else if(v.getId() == R.id.rightDirection){
                        ImageView image = new ImageView(getApplicationContext());
                        image.setImageResource(R.mipmap.right_arrow);
                        dropLayout.addView(image);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
                        image.setPadding(32,0 ,0 ,0);
                        image.setLayoutParams(params);
                        path[pathIndex++] = 2;

                    }
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    break;

                case DragEvent.ACTION_DROP:
                    break;
            }

            return true;
        }
    };
}

