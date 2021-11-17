package com.example.sdp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class BluetoothActivity extends AppCompatActivity {
    private ListView lstView1, lstView2;
    private ArrayAdapter<String> arrayAdapter1, arrayAdapter2;
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean isFindAvailabledClicked = false;
    private int REQUEST_ENABLE_BT = 1;  // used to identify adding bluetooth names

    // NEW STUFF
    private BluetoothSocket mBTSocket = null;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mAvailableDevicesListView;

    private Handler mHandler; // Our main handler that will receive callback notifications
//    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
//    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    // #defines for identifying shared types between calling functions
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        TextView textView2 = (TextView) findViewById(R.id.textView2);
        TextView pairedDevices = (TextView) findViewById(R.id.paired_devices);

        Button btnFindPaired = (Button) findViewById(R.id.button_find_paired);
        Button btnFindAvailable = (Button) findViewById(R.id.button_find_available);

        lstView1 = (ListView) findViewById(R.id.pairedDeviceList);
//        lstView2 = (ListView) findViewById(R.id.availableDeviceList);

        textView2.setVisibility(View.GONE);

        checkPermission();

        // NEW STUFF
        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mAvailableDevicesListView = (ListView) findViewById(R.id.availableDeviceList);
        mAvailableDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view


        btnFindPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btAdapter == null) {
                    btnFindPaired.setEnabled(false);
                    textView2.setVisibility(View.VISIBLE);
                }
                else {
                    btnFindPaired.setEnabled(true);
                    pairedDevices.setVisibility(View.VISIBLE);

                    if (!btAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }

                    // Get paired devices.
                    Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                    ArrayList list = new ArrayList();
                    if (pairedDevices.size() > 0) {
                        // There are paired devices. Get the name and address of each paired device.
                        for (BluetoothDevice device : pairedDevices) {
                            String deviceName = device.getName();
                            String deviceMACAddress = device.getAddress(); // MAC address
                            list.add("Name: " + deviceName + "\nMAC Address: " + deviceMACAddress);
                            arrayAdapter1 = new ArrayAdapter(BluetoothActivity.this, android.R.layout.simple_list_item_1, list) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                                    tv.setTextColor(Color.WHITE);
                                    return view;
                                }
                            };
                            lstView1.setAdapter(arrayAdapter1);
                        }
                    }
                    else {
                        list.add("There are no paired devices.");
                        arrayAdapter1 = new ArrayAdapter(BluetoothActivity.this, android.R.layout.simple_list_item_1, list) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                                tv.setTextColor(Color.WHITE);
                                return view;
                            }
                        };
                        lstView1.setAdapter(arrayAdapter1);
                    }
                }
            }
        });

        btnFindAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFindAvailabledClicked = true;
                mBTArrayAdapter.clear(); // clear items
                btAdapter.startDiscovery();

                // Create a BroadcastReceiver for ACTION_FOUND.
                final BroadcastReceiver receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        ArrayList list2 = new ArrayList();
                        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                            // Discovery has found a device. Get the BluetoothDevice
                            // object and its info from the Intent.
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                            // NEW STUFF

                            // add the name to the list
                            mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                            mBTArrayAdapter.notifyDataSetChanged();

//                            String deviceName = device.getName();
//                            String deviceMACAddress = device.getAddress(); // MAC address
//                            list2.add("Name: " + deviceName + "\nMAC Address: " + deviceMACAddress);
//                            arrayAdapter2 = new ArrayAdapter(BluetoothActivity.this, android.R.layout.simple_list_item_1, list2) {
//                                @Override
//                                public View getView(int position, View convertView, ViewGroup parent) {
//                                    View view = super.getView(position, convertView, parent);
//                                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
//                                    tv.setTextColor(Color.WHITE);
//                                    return view;
//                                }
//                            };
//                            lstView2.setAdapter(arrayAdapter2);
                        }
                    }
                };
                // Register for broadcasts when a device is discovered.
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

//                if (isFindAvailabledClicked) {
//                    Adapter.clear();
//                    getActivity().registerReceiver(bReciever, filter);
//                    bTAdapter.startDiscovery();
//                } else {
//                    getActivity().unregisterReceiver(bReciever);
//                    bTAdapter.cancelDiscovery();
//                }
                registerReceiver(receiver, filter);

//                filter
//
//                ArrayList list = new ArrayList();
//                for (BluetoothDevice device : receiver) {
//
//                }

            }
        });

//        lstView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if(((ApplicationEx)getApplication()).writeBt("Hello World\n".getBytes(StandardCharsets.UTF_8))){
//                    Toast.makeText(getApplicationContext(), "OK, sent", Toast.LENGTH_SHORT).show();
//                }
////                 ArrayList list = (ArrayList) parent.getAdapter().getItem(position);
//
//            }
//        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        } else {
            checkPermission();
        }
    }
}