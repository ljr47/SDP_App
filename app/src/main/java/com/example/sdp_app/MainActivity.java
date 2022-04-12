package com.example.sdp_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private Button connectBTLE, startCommand, endCommand, releaseCommand, clearFaultCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectBTLE = findViewById(R.id.button_connect_ble);
        startCommand = findViewById(R.id.button_start);
        endCommand = findViewById(R.id.button_end);
        releaseCommand = findViewById(R.id.button_release);
        clearFaultCommand = findViewById(R.id.button_clear);

        connectBTLE.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getSharedPreferences("mypref", 0);
        String mac = sharedPref.getString("macAdd", "");
        TextView deviceMAC = findViewById(R.id.device_mac);
        deviceMAC.setText(mac);
        connectBTLE.setVisibility(View.VISIBLE);

        connectBTLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().addOnBackStackChangedListener(MainActivity.this);
                Bundle args = new Bundle();
//                args.putString("device", globalVar.getBtDeviceMACAddress());
                args.putString("device", mac);
                Fragment fragment = new TerminalFragment();
                fragment.setArguments(args);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_terminal, fragment);
                ft.commit();
            }
        });

        startCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = "*s*";
                TerminalFragment fragment = (TerminalFragment) getSupportFragmentManager().findFragmentById(R.id.frame_terminal);
                fragment.send(command);
            }
        });

        endCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = "*e*";
                TerminalFragment fragment = (TerminalFragment) getSupportFragmentManager().findFragmentById(R.id.frame_terminal);
                fragment.send(command);
            }
        });

        releaseCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = "*r*";
                TerminalFragment fragment = (TerminalFragment) getSupportFragmentManager().findFragmentById(R.id.frame_terminal);
                fragment.send(command);
            }
        });

        clearFaultCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = "*c*";
                TerminalFragment fragment = (TerminalFragment) getSupportFragmentManager().findFragmentById(R.id.frame_terminal);
                fragment.send(command);
            }
        });

    }

    public void openBluetoothLE(View view) {
        Intent intent = new Intent(MainActivity.this, BluetoothLEActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackStackChanged() {

    }
}