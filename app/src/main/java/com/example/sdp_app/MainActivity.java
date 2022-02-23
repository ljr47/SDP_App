package com.example.sdp_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private Button connectBTLE, startCommand, endCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectBTLE = findViewById(R.id.button_connect_ble);
        startCommand = findViewById(R.id.button_start);
        endCommand = findViewById(R.id.button_end);

        connectBTLE.setVisibility(View.INVISIBLE);


//        getSupportFragmentManager().beginTransaction().add(R.id.terminalFrag, new TerminalFragment(), "terminal");

//        Button btn = findViewById(R.id.button_send);
//        btn.setOnClickListener(v -> {
//            EditText txt = findViewById(R.id.bluetooth_message);
//            String btMessage = txt.getText().toString() + "\n";
//            if(((ApplicationEx)getApplication()).writeBt(btMessage.getBytes(StandardCharsets.UTF_8))){
//                Toast.makeText(getApplicationContext(), "Message sent.", Toast.LENGTH_SHORT).show();
//                txt.setText("");
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        final ApplicationEx globalVar = (ApplicationEx) getApplicationContext();

//        TextView deviceName = findViewById(R.id.device_name);
        TextView deviceMAC = findViewById(R.id.device_mac);
        deviceMAC.setText(globalVar.getBtDeviceMACAddress());

        connectBTLE.setVisibility(View.VISIBLE);

        connectBTLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().addOnBackStackChangedListener(MainActivity.this);
                Bundle args = new Bundle();
                args.putString("device", globalVar.getBtDeviceMACAddress());
                Fragment fragment = new TerminalFragment();
                fragment.setArguments(args);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.frame_terminal, fragment);
                ft.commit();
//        getSupportFragmentManager().beginTransaction().add(R.id.terminalFrag, new TerminalFragment(), "terminal");

//        deviceName.setText(globalVar.getBtDeviceName());
            }
        });

        startCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = "*str*";
                TerminalFragment fragment = (TerminalFragment) getSupportFragmentManager().findFragmentById(R.id.frame_terminal);
                fragment.send(command);
            }
        });

        endCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String command = "*end*";
                TerminalFragment fragment = (TerminalFragment) getSupportFragmentManager().findFragmentById(R.id.frame_terminal);
                fragment.send(command);
            }
        });

    }

    public void openBluetooth(View view) {
        Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
        startActivity(intent);
    }

    public void openBluetoothLE(View view) {
        Intent intent = new Intent(MainActivity.this, BluetoothLEActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackStackChanged() {

    }
}