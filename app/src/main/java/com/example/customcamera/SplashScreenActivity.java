package com.example.customcamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.customcamera.controller.MainActivity;


public class SplashScreenActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    int SPLASH_TIME_OUT = 1000;
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        Intent main = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(main);
        finish();
      }
    }, SPLASH_TIME_OUT);
  }
}