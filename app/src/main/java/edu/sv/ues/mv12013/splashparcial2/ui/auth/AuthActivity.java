package edu.sv.ues.mv12013.splashparcial2.ui.auth;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import edu.sv.ues.mv12013.splashparcial2.R;
import edu.sv.ues.mv12013.splashparcial2.ui.auth.login.LoginFragment;

public class AuthActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth); // contenedor FrameLayout
        if (savedInstanceState == null) {
            replace(new LoginFragment(), false);
        }
    }

    public void replace(Fragment fragment, boolean addToBackStack) {
        var tx = getSupportFragmentManager().beginTransaction()
                .replace(R.id.auth_container, fragment);
        if (addToBackStack) tx.addToBackStack(fragment.getClass().getSimpleName());
        tx.commit();
    }
}