package com.example.sdp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class BluetoothLEActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private PreviewView previewView;
    private Button qrScanButton, qrCodeFoundButton, connectButton;
    private TextView macAddress;
    private String qrCode;

    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_le);

        final  ApplicationEx globalVar = (ApplicationEx) getApplicationContext();

        // Set widgets
        previewView = findViewById(R.id.previewView_scanner);
        qrScanButton = findViewById(R.id.button_qrCodeScan);
        qrCodeFoundButton = findViewById(R.id.button_qrCodeFound);
        macAddress = findViewById(R.id.bluetoothLE_mac);
//        connectButton = findViewById(R.id.button_connect_ble);

        qrCodeFoundButton.setVisibility(View.GONE);
        previewView.setVisibility(View.GONE);

        checkPermission();

        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        qrScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewView.setVisibility(View.VISIBLE);
                cameraProviderFuture = ProcessCameraProvider.getInstance(BluetoothLEActivity.this);
                requestCamera();
            }
        });

        qrCodeFoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), qrCode, Toast.LENGTH_SHORT).show();
                Log.i(MainActivity.class.getSimpleName(), "QR Code Found: " + qrCode);
                macAddress.setText(qrCode);
                globalVar.setBtDeviceMACAddress(qrCode);
            }
        });

//        connectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                globalVar.setBtDeviceMACAddress(qrCode);
////                Bundle args = new Bundle();
////                args.putString("device", macAddress.toString());
////                Fragment fragment = new TerminalFragment();
////                fragment.setArguments(args);
////                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment, "terminal-c").addToBackStack(null).commit();
////                Intent intent = new Intent(v.getContext(), MainActivity.class);
////                startActivity(intent);
//            }
//        });
    }

    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(BluetoothLEActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Toast.makeText(this, "Error starting camera " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraPreview(@NonNull ProcessCameraProvider cameraProvider) {
        previewView.setImplementationMode(PreviewView.ImplementationMode.PERFORMANCE);

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new QRCodeImageAnalyzer(new QRCodeFoundListener() {
            @Override
            public void onQRCodeFound(String _qrCode) {
                qrCode = _qrCode;
                qrCodeFoundButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void qrCodeNotFound() {
                qrCodeFoundButton.setVisibility(View.INVISIBLE);
            }
        }));

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageAnalysis, preview);
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,}, 1);
            }
        }
    }
}
