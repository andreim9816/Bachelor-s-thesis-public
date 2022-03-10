package licenta.allbank.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import licenta.allbank.R;
import licenta.allbank.ui.home.HomeFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HomeFragment.appJustStarted = true;
        HomeFragment.loadedAccountsData = 2;
    }
}