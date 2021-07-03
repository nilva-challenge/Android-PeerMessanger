package com.example.nilvabtchallenge;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private View bluetoothNavHost;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navController = Navigation.findNavController(MainActivity.this, R.id.navHostBluetoothFragment);
        bluetoothNavHost = findViewById(R.id.navHostBluetoothFragment);

    }
}