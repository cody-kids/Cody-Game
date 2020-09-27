package com.example.dragtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    Animation topAnimation , bottomAnimation, fadeInAnimation;
    ImageView ninjaImage, shirneImage;
    TextView mainText, sloganText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_animation);

        ninjaImage = (ImageView) findViewById(R.id.ninja);

        shirneImage = (ImageView) findViewById(R.id.shrine);
        mainText = (TextView) findViewById(R.id.mainText);
        sloganText = (TextView) findViewById(R.id.slogan);

        ninjaImage.setAnimation(bottomAnimation);
        shirneImage.setAnimation(topAnimation);
        mainText.setAnimation(topAnimation);
        sloganText.setAnimation(topAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainPageActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2500);

    }
}