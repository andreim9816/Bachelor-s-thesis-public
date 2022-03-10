package licenta.allbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Splash", "START");
        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        Log.i("Splash", "FINISH");
        finish();
    }
}