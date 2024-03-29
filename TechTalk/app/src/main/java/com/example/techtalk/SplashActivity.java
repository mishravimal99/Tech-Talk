package com.example.techtalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.techtalk.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private ImageView ivSplash;
    private TextView tvSplash;
    private Animation animation1, animation2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(getSupportActionBar()!=null){
            getSupportActionBar().hide();
        }

        ivSplash = findViewById(R.id.ivSplash);
        tvSplash = findViewById(R.id.tvSplash);
        animation1 = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        animation2 = AnimationUtils.loadAnimation(this, R.anim.splash_name_animation);

        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ivSplash.startAnimation(animation1);
        tvSplash.startAnimation(animation2);
    }
}