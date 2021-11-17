package com.example.sdp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private BluetoothDevice selectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.test_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((ApplicationEx)getApplication()).writeBt("Hello World\n".getBytes(StandardCharsets.UTF_8))){
                    Toast.makeText(getApplicationContext(), "OK, sent", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void openBluetooth(View view) {
        Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
        startActivity(intent);
//        startActivityForResult(intent, selectedDevice);
    }

//    public void openBluetooth(View view) {
//        Intent intent =  new Intent(this, BluetoothActivity.class);
//        startActivity(intent);
//    }
}