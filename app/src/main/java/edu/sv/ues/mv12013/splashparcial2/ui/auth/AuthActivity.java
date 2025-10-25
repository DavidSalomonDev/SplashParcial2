package edu.sv.ues.mv12013.splashparcial2.ui.auth;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;

import edu.sv.ues.mv12013.splashparcial2.R;
import edu.sv.ues.mv12013.splashparcial2.ui.auth.login.LoginFragment;

public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Asegurar que las barras del sistema sean visibles
        setupSystemBars();

        if (savedInstanceState == null) {
            replace(new LoginFragment(), false);
        }
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

    public void replace(Fragment fragment, boolean addToBackStack) {
        var tx = getSupportFragmentManager().beginTransaction()
                .replace(R.id.auth_container, fragment);
        if (addToBackStack) tx.addToBackStack(fragment.getClass().getSimpleName());
        tx.commit();
    }
}