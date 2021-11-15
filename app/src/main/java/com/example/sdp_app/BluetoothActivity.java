package com.example.sdp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {
    private ListView lstView1, lstView2;
    private ArrayAdapter arrayAdapter1, arrayAdapter2;
    private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean isFindAvailabledClicked = false;
    private int REQUEST_ENABLE_BT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        TextView textView2 = (TextView) findViewById(R.id.textView2);
        TextView pairedDevices = (TextView) findViewById(R.id.paired_devices);

        Button btnFindPaired = (Button) findViewById(R.id.button_find_paired);
        Button btnFindAvailable = (Button) findViewById(R.id.button_find_available);

        lstView1 = (ListView) findViewById(R.id.pairedDeviceList);
        lstView2 = (ListView) findViewById(R.id.availableDeviceList);

        textView2.setVisibility(View.GONE);

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
                            String deviceName = device.getName();
                            String deviceMACAddress = device.getAddress(); // MAC address
                            list2.add("Name: " + deviceName + "\nMAC Address: " + deviceMACAddress);
                            arrayAdapter2 = new ArrayAdapter(BluetoothActivity.this, android.R.layout.simple_list_item_1, list2) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                                    tv.setTextColor(Color.WHITE);
                                    return view;
                                }
                            };
                            lstView2.setAdapter(arrayAdapter2);
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
    }






//    public void findDevices(View view) {
////        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//
//        if(!btAdapter.isDiscovering()){
//            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE),1);
//
//            Snackbar.make(findViewById(R.id.bluetoothActivity), R.string.device_discoverable,
//                    Snackbar.LENGTH_SHORT)
//                    .show();
//        }

//        // Get paired devices.
//        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
//        ArrayList list = new ArrayList();
//        if (pairedDevices.size() > 0) {
//            // There are paired devices. Get the name and address of each paired device.
//            for (BluetoothDevice device : pairedDevices) {
//                String deviceName = device.getName();
//                String deviceMACAddress = device.getAddress(); // MAC address
//                list.add("Name: " + deviceName + " MAC Address: " + deviceMACAddress);
//                arrayAdapter1 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, list);
//                lstView1.setAdapter(arrayAdapter1);
//            }
//        }

//        IntentFilter filter = new IntentFilter();
//
//        filter.addAction(BluetoothDevice.ACTION_FOUND);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//
//        registerReceiver(receiver, filter);
//        adapter.startDiscovery();
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        // Don't forget to unregister the ACTION_FOUND receiver.
//        unregisterReceiver(receiver);
//    }
}