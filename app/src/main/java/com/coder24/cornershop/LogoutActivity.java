package com.coder24.cornershop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.coder24.cornershop.ui.logout.LogoutFragment;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logout_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, LogoutFragment.newInstance())
                    .commitNow();
        }
    }
}
