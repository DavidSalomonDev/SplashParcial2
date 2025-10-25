package edu.sv.ues.mv12013.splashparcial2.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;

import edu.sv.ues.mv12013.splashparcial2.R;
import edu.sv.ues.mv12013.splashparcial2.ui.home.HomeActivity;
import edu.sv.ues.mv12013.splashparcial2.ui.auth.AuthActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 3000L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_SplashParcial2);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper())
                .postDelayed(this::routeFromSession, SPLASH_DURATION_MS);
    }

    private void routeFromSession() {
        boolean isLoggedIn = getSharedPreferences("auth_prefs", MODE_PRIVATE)
                .getBoolean("logged_in", false);

        Intent intent = new Intent(this, isLoggedIn ? HomeActivity.class : AuthActivity.class);
        startActivity(intent);
        finish();
    }
}