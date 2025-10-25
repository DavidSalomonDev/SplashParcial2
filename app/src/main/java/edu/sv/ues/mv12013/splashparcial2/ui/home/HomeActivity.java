package edu.sv.ues.mv12013.splashparcial2.ui.home;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import edu.sv.ues.mv12013.splashparcial2.R;
import edu.sv.ues.mv12013.splashparcial2.ui.profile.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        if (item.getItemId() == R.id.action_profile) {
            replace(new ProfileFragment(), true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void replace(Fragment fragment, boolean addToBackStack) {
        var tx = getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_container, fragment);
        if (addToBackStack) tx.addToBackStack(fragment.getClass().getSimpleName());
        tx.commit();
    }
}