package com.example.dragtest;

import android.util.Log;

public class lagMove extends Thread {

    public void run(){
        try {
            sleep(100);
            Log.e("Thread message", "Running lag thread");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e("Thread message", "Error while running lag thread", e);

        }
    }
}
