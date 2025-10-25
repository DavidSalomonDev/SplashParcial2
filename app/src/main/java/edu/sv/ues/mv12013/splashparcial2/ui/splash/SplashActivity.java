package edu.sv.ues.mv12013.splashparcial2.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import edu.sv.ues.mv12013.splashparcial2.ui.auth.AuthActivity;
import edu.sv.ues.mv12013.splashparcial2.ui.home.HomeActivity;
import androidx.core.splashscreen.SplashScreen;

public class SplashActivity extends AppCompatActivity {


    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        navigate();
    }

    private void navigate() {
        boolean loggedIn = FirebaseAuth.getInstance().getCurrentUser() != null;
        Intent i = new Intent(this, loggedIn ? HomeActivity.class : AuthActivity.class);
        startActivity(i);
        finish();
    }
}