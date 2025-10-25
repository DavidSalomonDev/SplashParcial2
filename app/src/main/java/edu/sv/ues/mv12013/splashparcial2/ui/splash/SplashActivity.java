package edu.sv.ues.mv12013.splashparcial2.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

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

        // Asegurar que las barras del sistema sean visibles
        setupSystemBars();

        new Handler(Looper.getMainLooper())
                .postDelayed(this::routeFromSession, SPLASH_DURATION_MS);
    }

    private void setupSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        View decorView = getWindow().getDecorView();
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), decorView);
        if (controller != null) {
            controller.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            );
        }
    }

    private void routeFromSession() {
        boolean isLoggedIn = getSharedPreferences("auth_prefs", MODE_PRIVATE)
                .getBoolean("logged_in", false);

        Intent intent = new Intent(this, isLoggedIn ? HomeActivity.class : AuthActivity.class);
        startActivity(intent);
        finish();
    }
}