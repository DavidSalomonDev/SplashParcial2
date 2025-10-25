package edu.sv.ues.mv12013.splashparcial2.ui.home;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import edu.sv.ues.mv12013.splashparcial2.R;
import edu.sv.ues.mv12013.splashparcial2.data.repository.UserRepository;
import edu.sv.ues.mv12013.splashparcial2.ui.auth.AuthActivity;
import edu.sv.ues.mv12013.splashparcial2.ui.profile.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    private UserRepository userRepository;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userRepository = new UserRepository(getApplicationContext());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_container, new HomeFragment())
                    .commit();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_profile) {
            replace(new ProfileFragment(), true);
            return true;
        } else if (id == R.id.action_logout) {
            performLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performLogout() {
        // 1) Sign out Firebase + limpiar prefs + limpiar Room
        userRepository.logout();

        // 2) Ir a AuthActivity limpiando el back stack
        Intent i = new Intent(this, AuthActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);

        // 3) Cerrar HomeActivity
        finish();
    }

    public void replace(Fragment fragment, boolean addToBackStack) {
        var tx = getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_container, fragment);
        if (addToBackStack) tx.addToBackStack(fragment.getClass().getSimpleName());
        tx.commit();
    }
}