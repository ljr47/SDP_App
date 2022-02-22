package com.example.sdp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btn = findViewById(R.id.button_send);
        btn.setOnClickListener(v -> {
            EditText txt = findViewById(R.id.bluetooth_message);
            String btMessage = txt.getText().toString() + "\n";
            if(((ApplicationEx)getApplication()).writeBt(btMessage.getBytes(StandardCharsets.UTF_8))){
                Toast.makeText(getApplicationContext(), "Message sent.", Toast.LENGTH_SHORT).show();
                txt.setText("");
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        final ApplicationEx globalVar = (ApplicationEx) getApplicationContext();

        TextView deviceName = findViewById(R.id.device_name);
        TextView deviceMAC = findViewById(R.id.device_mac);

        deviceName.setText(globalVar.getBtDeviceName());
        deviceMAC.setText(globalVar.getBtDeviceMACAddress());

    }

    public void openBluetooth(View view) {
        Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
        startActivity(intent);
    }

    public void openBluetoothLE(View view) {
        Intent intent = new Intent(MainActivity.this, BluetoothLEActivity.class);
        startActivity(intent);
    }
}