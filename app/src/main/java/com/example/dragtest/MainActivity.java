package com.example.dragtest;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TintableImageSourceView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    /*Global declaration*/
    ImageView upArrow, downArrow, leftArrow, rightArrow, player;
    Button startButton;
    LinearLayout dropLayout;
    GridLayout gridLayout;

    /*To check for collision*/
    boolean flag = false;

    /*To store respected values*/
    public int position , row, column, end, pathIndex = 0,  rotationIndex = 1 ,startIndex, endIndex;

    public int[] size = new int[100];


    /*To store the path given by the user*/
    public int[] path = new int[100];

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Initialization of controls*/
        upArrow = (ImageView) findViewById(R.id.upDirection);
        downArrow = (ImageView) findViewById(R.id.downDirection);
        leftArrow = (ImageView) findViewById(R.id.leftDirection);
        rightArrow = (ImageView) findViewById(R.id.rightDirection);
        startButton = (Button) findViewById(R.id.startButton);

        /*Initialization of layout to drop the controls*/
        dropLayout = (LinearLayout) findViewById(R.id.dropLayout);


        /*Setting up long click listener to allow the controls to be dragged */
        upArrow.setOnLongClickListener(longClickListener);
        downArrow.setOnLongClickListener(longClickListener);
        leftArrow.setOnLongClickListener(longClickListener);
        rightArrow.setOnLongClickListener(longClickListener);

        /*Setiing drag listener to allow the layout to accept the controls being dragged*/
        dropLayout.setOnDragListener(dragListener);

        /*Declaring json String and reading the file from assets folder*/
        String json = loadJSONFromAsset();

        /*Extracting details form the json file*/
        try{

            /*Initializing to go to root of level*/
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.getJSONArray("level");
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            /*Initializing to go to root of grid*/
            JSONObject grid = jsonObject.getJSONObject("grid");

            /*Fetching details about the grid layout*/
            row = grid.getInt("rows");
            column = grid.getInt("cols");

            /*Initializing to go the root of path*/
            JSONArray path = jsonObject.getJSONArray("path");

            /*Fetching start and end index of the path*/
            startIndex = jsonObject.getInt("start");
            endIndex = jsonObject.getInt("end");

            /*Initializing the fields*/
            position = startIndex;
            end = endIndex;
            gridLayout = findViewById(R.id.gridLayout);
            gridLayout.setColumnCount(column);
            gridLayout.setRowCount(row);

            /*Declaring parameter to start build*/
            int i = 1, pathIndex = 0, sizeIndex = 0;

            /*Building Grid layout*/
            for(int r = 1; r <= row; r++){
                for(int c = 1; c <= column; c++ ) {

                    /*Creating new ImageView for the GridView*/
                    ImageView imageView = new ImageView(this);

                    /*Getting index of the path*/
                    JSONObject pathId = path.getJSONObject(pathIndex);
                    int id = pathId.getInt("id");

                    /*Storing the path*/
                    size[sizeIndex++] = id;

                    /*Checking for constraint*/
                    if(pathIndex < (row * column)){

                        /*Checking for path and putting tiles*/
                        if(id == i){
                            imageView.setImageResource(R.mipmap.tile);
                            pathIndex++;
                        }

                        /*Putting crate*/
                        else imageView.setImageResource(R.mipmap.crate);

                    }

                    /*Putting character in start index */
                    if(pathIndex == startIndex){
                        imageView.setImageResource(R.mipmap.up);
                    }

                   

                    /*Setting id of each image*/
                    imageView.setId(i++);

                    /*Default properties to make the grid*/
                    GridLayout.Spec rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);
                    GridLayout.Spec colSpan = GridLayout.spec(GridLayout.UNDEFINED, 1);



                    if (r == 0 && c == 0) {
                        Log.e("", "specs");
                        rowSpan = GridLayout.spec(GridLayout.UNDEFINED, 2);
                        colSpan = GridLayout.spec(GridLayout.UNDEFINED, 2);
                    }
                    GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams(rowSpan, colSpan);
                    gridLayout.addView(imageView, gridParams);

                    imageView.requestLayout();

                    imageView.getLayoutParams().height = 150;
                    imageView.getLayoutParams().width = 150;

                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                }
            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        /*Making a thread to run the commands*/


        /*Setting on Click listener for start button */
        /*Todo : Change start button to flag*/
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Making thread each time   */
                final Thread running = new Thread(){
                    @Override
                    public void run(){
                        try {
                            runIt();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

                if(running.isAlive()){
                    Toast.makeText(MainActivity.this, "Started", Toast.LENGTH_SHORT).show();

                }
                else{
                    running.start();
                    Toast.makeText(MainActivity.this, "Started", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /*Making values same as start*/
     public void resetGame(){

        /*Setting the image at position to tile*/
        ImageView dead = findViewById(position);
        dead.setImageResource(R.mipmap.crate);

        /*Setting the rotation to up and setting the image to up direction */
        rotationIndex = 1;
        position = startIndex;
        ImageView player = findViewById(startIndex);
        player.setImageResource(R.mipmap.up);

        int dropIdS = 1000;

        for(int i = 0; i < path.length; i++){
            ImageView imageView = (ImageView) findViewById(dropIdS++);
            imageView.setVisibility(View.GONE);
        }

    }

    /*Main function handling the commands*/
    void runIt() throws InterruptedException {

        /*Getting details of the current thread*/
        Thread current = Thread.currentThread();
        current.sleep(2000);

        /*Looping each dragged command by user*/
        for(int i = 0; i < path.length ; i++){

            /*Switch case for path direction*/
            switch (path[i]){
                case 1:

                    goUp();
                    current.sleep(500);
                    break;

                case -1:
                    goDown();

                    current.sleep(500);

                    break;

                case -2:
                    goLeft();

                    current.sleep(500);

                    break;

                case 2:
                    goRight();

                    current.sleep(500);

                    break;
            }

        }
    }

    void goUp(){

        /*Collision check for out of bounds. Inititated with no collision*/
        boolean check = true;

        /*Checking for collision for out of bounds and if then assigning to false*/
        for(int i = 1; i <= column; i++){
            if(position == i){
                check = false;
                break;
            }
        }

        /*If no collision*/
        if(check){

            /*Getting player current position*/
            player = (ImageView) findViewById(position);

            /*Checking for collision for wrong path*/
            for(int i = 0; i < size.length ; i++){
                if(size[i] == (position - row)){
                    flag = true;
                    break;
                }
            }

            /*If no collision*/
            if(flag){

                /*Moving player to new position*/
                player.setImageResource(R.mipmap.tile);
                position = position - row;
                ImageView newPosition = (ImageView) findViewById(position);
                newPosition.setImageResource(R.mipmap.up);

                /*Checking for the end position of path*/
                if( position == end){

                    /*Running toast on ui thread*/
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Won", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

                /*Making wrong path collision as false for next command*/
                flag = false;
            }

            /*Collision with obstacle*/
            else {

                /*Setting the current image as tile*/
                player.setImageResource(R.mipmap.tile);

                /*Updating position*/
                position= position - row;

                /*Setting image in new position*/
                ImageView newPosition = (ImageView) findViewById(position);
                newPosition.setImageResource(R.mipmap.player_dead);

                /*Running toast on ui thread*/
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Lost", Toast.LENGTH_SHORT).show();
                    }
                });

                /*Resetting the game*/
//                resetGame();

                Intent popUp = new Intent(getApplicationContext(), PopUpActivity.class);
                startActivity(popUp);
            }
        }

        /*Collision: Out of bounds*/
        else{
            /*Running toast on ui thread*/
            this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Out of Bounds", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Log.e("Position: ", Integer.toString(position));
    }

    void goDown() throws InterruptedException {

        /*Collision check for out of bounds. Inititated with no collision*/
        boolean check = true;

        /*Checking for collision for out of bounds and if then assigning to false*/
        for(int i = (column*row - (column - 1)); i <= column * row; i++){
            if(position == i){
                check = false;
                break;
            }
        }

        /*If no collision*/
        if(check){

            /*Getting player current position*/
            player = (ImageView) findViewById(position);

            /*Checking for collision for wrong path*/
            for(int i = 0; i < size.length ; i++){
                if(size[i] == (position + row)){
                    flag = true;
                    break;
                }
            }

            /*If no collision*/
            if(flag){

                /*Moving player to new position*/
                player.setImageResource(R.mipmap.tile);
                position = position + row;
                ImageView newPosition = (ImageView) findViewById(position);
                newPosition.setImageResource(R.mipmap.down);

                /*Checking for the end position of path*/
                if( position == end){

                    /*Running toast on ui thread*/
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Won", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

                /*Making wrong path collision as false for next command*/
                flag = false;
            }

            /*Collision with obstacle*/
            else {

                /*Setting the current image as tile*/
                player.setImageResource(R.mipmap.tile);

                /*Updating position*/
                position = position + row;

                /*Setting image in new position*/
                ImageView newPosition = (ImageView) findViewById(position);
                newPosition.setImageResource(R.mipmap.player_dead);

                Thread current = Thread.currentThread();
                current.sleep(1000);

                /*Running toast on ui thread*/
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Lost", Toast.LENGTH_SHORT).show();
                    }
                });

                current.sleep(1000);

                /*Resetting the game*/
//                resetGame();

                Intent popUp = new Intent(getApplicationContext(), PopUpActivity.class);
                startActivity(popUp);
            }
        }

        /*Collision: Out of bounds*/
        else{

            /*Running toast on ui thread*/
            this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Out of Bounds", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Log.e("Position: ", Integer.toString(position));
    }

    void goRight(){

        /*Collision check for out of bounds. Inititated with no collision*/
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
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Won", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                ImageView newPosition = (ImageView) findViewById(position);
                newPosition.setImageResource(R.mipmap.right);

                flag = false;
            }
            else {
                player.setImageResource(R.mipmap.tile);
                position++;
                ImageView newPosition = (ImageView) findViewById(position);
                newPosition.setImageResource(R.mipmap.player_dead);
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Lost", Toast.LENGTH_SHORT).show();
                    }
                });
                resetGame();
            }
        }
        else{

            this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Out of bounds", Toast.LENGTH_SHORT).show();
                }
            });
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
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Won", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                ImageView newPosition = (ImageView) findViewById(position);
                newPosition.setImageResource(R.mipmap.left);

                flag = false;
            }
            else {
                player.setImageResource(R.mipmap.tile);
                position--;
                ImageView newPosition = (ImageView) findViewById(position);
                newPosition.setImageResource(R.mipmap.player_dead);
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Lost", Toast.LENGTH_SHORT).show();
                    }
                });
                resetGame();
            }
        }
        else{
            this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Out of bounds", Toast.LENGTH_SHORT).show();
                }
            });
        }
        Log.e("Position: ", Integer.toString(position));
    }

    void rotateLeft(){
        player = (ImageView) findViewById(position);

        switch (rotationIndex){
            case 1:
                player.setImageResource(R.mipmap.left);
                rotationIndex = -2;
                break;

            case -1:
                player.setImageResource(R.mipmap.right);
                rotationIndex = 2;
                break;

            case 2:
                player.setImageResource(R.mipmap.up);
                rotationIndex = 1;
                break;

            case -2:
                player.setImageResource(R.mipmap.down);
                rotationIndex = -1;
                break;
        }
    }

    void rotateRight(){
        player = (ImageView) findViewById(position);

        switch (rotationIndex){
            case 1:
                player.setImageResource(R.mipmap.right);
                rotationIndex = 2;
                break;

            case -1:
                player.setImageResource(R.mipmap.left);
                rotationIndex = -2;
                break;

            case 2:
                player.setImageResource(R.mipmap.down);
                rotationIndex = -1;
                break;

            case -2:
                player.setImageResource(R.mipmap.up);
                rotationIndex = 1;
                break;
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
            int dropId = 1000;

            switch (event){
                case DragEvent.ACTION_DRAG_ENTERED :
                    final View v = (View) dragEvent.getLocalState();
                    if(v.getId() == R.id.upDirection){
                        ImageView image = new ImageView(getApplicationContext());
                        image.setImageResource(R.mipmap.up_arrow);
                        image.setId(dropId++);
                        dropLayout.addView(image);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
                        image.setPadding(16,0 ,0 ,0);
                        image.setLayoutParams(params);
                        path[pathIndex++] = 1;


                    }

                    else if(v.getId() == R.id.downDirection){
                        final ImageView image = new ImageView(getApplicationContext());
                        image.setImageResource(R.mipmap.down_arrow);
                        image.setId(dropId++);
                        dropLayout.addView(image);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
                        image.setPadding(16,0 ,0 ,0);
                        image.setLayoutParams(params);
                        path[pathIndex++] = -1;


                    }

                    else if(v.getId() == R.id.leftDirection){
                        final ImageView image = new ImageView(getApplicationContext());
                        image.setImageResource(R.mipmap.left_arrow);
                        image.setId(dropId++);
                        dropLayout.addView(image);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
                        image.setPadding(16,0 ,0 ,0);
                        image.setLayoutParams(params);
                        path[pathIndex++] = -2;


                    }

                    else if(v.getId() == R.id.rightDirection){
                        final ImageView image = new ImageView(getApplicationContext());
                        image.setImageResource(R.mipmap.right_arrow);
                        image.setId(dropId++);
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

