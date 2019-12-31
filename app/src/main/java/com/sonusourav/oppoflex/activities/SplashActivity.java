package com.sonusourav.oppoflex.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.sonusourav.oppoflex.R;
import com.sonusourav.oppoflex.Utils.PreferenceManager;
import com.sonusourav.oppoflex.account.IntroScreen;
import com.sonusourav.oppoflex.account.LoginActivity;

public class SplashActivity extends AppCompatActivity {

  public static String TAG = SplashActivity.class.getSimpleName();
  private Intent splashIntent;
  private PreferenceManager splashPref;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.activity_splash);

    if (savedInstanceState != null) {
      onRestoreInstanceState(savedInstanceState);
    }

    splashInit();

    if (splashPref.isLoggedIn()) {
      Log.d(TAG, "loggedIn");

      if (splashPref.getAuthType().equals("emailAuth")) {
        Log.d(TAG, "emailAuth");
        goToActivity(2);

      } else if (splashPref.getAuthType().equals("googleAuth")) {
        Log.d(TAG, "googleAuth");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
          Log.d(TAG, "accountNotNull");
          goToActivity(2);
        } else {
          Log.d(TAG, "accountNull");
          goToActivity(1);
        }

      } else {
        goToActivity(1);
        Log.d(TAG, "else");
      }
    } else {
      if (splashPref.isFirstTimeLaunch()) {
        Log.d(TAG, "firstTimelaunch");
        goToActivity(0);
      } else {
        Log.d(TAG, "loggedOut");
        goToActivity(1);
      }
    }

    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        startActivity(splashIntent);
        overridePendingTransition(R.anim.fade_in, 0);
        finish();
      }
    }, 4600);
  }

  private void goToActivity(int activity) {

    switch (activity) {
      case 0:
        splashIntent = new Intent(SplashActivity.this, IntroScreen.class);
        break;
      case 2:
        splashIntent = new Intent(SplashActivity.this, MainActivity.class);
        break;
      default:
        splashIntent = new Intent(SplashActivity.this, LoginActivity.class);
        break;
    }
  }

  private void splashInit() {
    splashPref = new PreferenceManager(this);
    ImageView splash = findViewById(R.id.splash_iv);

    Glide.with(this)
        .load(R.drawable.gif_splash)
        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
        .into(splash);
  }

}
