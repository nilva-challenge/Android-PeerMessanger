package com.example.nilvabtchallenge;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.nilvabtchallenge.model.BluetoothDeviceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {

    private static final String TAG = "DeviceListActivity";

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothAdapter bluetoothAdapter;

    private ArrayAdapter<String> newDevicesArrayAdapter;
    private List<BluetoothDeviceModel> pairLists = new ArrayList<>();
    private List<BluetoothDeviceModel> availableList = new ArrayList<>();

    private RecyclerView pairRecyclerView, availableRecyclerView;
    private PairDeviceAdapter pairDeviceAdapter;
    private AvailableDeviceAdapter availableDeviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.device_list_activity_layout);

        setResult(Activity.RESULT_CANCELED);

        pairRecyclerView = findViewById(R.id.paired_devices);
        availableRecyclerView = findViewById(R.id.new_devices);

        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });
        newDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);



        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                BluetoothDeviceModel model = new BluetoothDeviceModel(device.getName(), device.getAddress());
                pairLists.add(model);

            }
            pairDeviceAdapter = new PairDeviceAdapter(getBaseContext(), new AddressListener() {
                @Override
                public void onAddressListener(String address) {
                    bluetoothAdapter.cancelDiscovery();
                    Intent intent1 = new Intent();
                    intent1.putExtra(EXTRA_DEVICE_ADDRESS, address);

                    DeviceListActivity.this.setResult(Activity.RESULT_OK, intent1);
                    finish();

                }
            });
            pairRecyclerView.setAdapter(pairDeviceAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
            pairRecyclerView.setLayoutManager(layoutManager);
            pairDeviceAdapter.addPairDevice(pairLists);

        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();

            BluetoothDeviceModel model1 = new BluetoothDeviceModel(noDevices, "");
            pairLists.add(model1);

            pairDeviceAdapter = new PairDeviceAdapter(getBaseContext(), new AddressListener() {
                @Override
                public void onAddressListener(String address) {
                    bluetoothAdapter.cancelDiscovery();

                    Intent intent1 = new Intent();
                    intent1.putExtra(EXTRA_DEVICE_ADDRESS, address);

                    DeviceListActivity.this.setResult(Activity.RESULT_OK, intent1);
                    finish();
                }
            });
            pairRecyclerView.setAdapter(pairDeviceAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
            pairRecyclerView.setLayoutManager(layoutManager);
            pairDeviceAdapter.addPairDevice(pairLists);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }

        this.unregisterReceiver(mReceiver);
    }


    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");

        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    BluetoothDeviceModel model = new BluetoothDeviceModel(device.getName(), device.getAddress());
                    availableList.add(model);

                    availableDeviceAdapter = new AvailableDeviceAdapter(getBaseContext(), new AddressListener() {
                        @Override
                        public void onAddressListener(String address) {
                            bluetoothAdapter.cancelDiscovery();

                            Intent intent1 = new Intent();
                            intent1.putExtra(EXTRA_DEVICE_ADDRESS, address);

                            DeviceListActivity.this.setResult(Activity.RESULT_OK, intent1);
                            finish();
                        }
                    });
                    availableRecyclerView.setAdapter(availableDeviceAdapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
                    availableRecyclerView.setLayoutManager(layoutManager);
                    availableDeviceAdapter.addAvailableDevice(availableList);

                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (newDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    newDevicesArrayAdapter.add(noDevices);
                    BluetoothDeviceModel model1 = new BluetoothDeviceModel(noDevices, "");
                    availableList.add(model1);

                    availableDeviceAdapter = new AvailableDeviceAdapter(getBaseContext(), new AddressListener() {
                        @Override
                        public void onAddressListener(String address) {
                            bluetoothAdapter.cancelDiscovery();

                            Intent intent1 = new Intent();
                            intent1.putExtra(EXTRA_DEVICE_ADDRESS, address);

                            DeviceListActivity.this.setResult(Activity.RESULT_OK, intent1);
                            finish();
                        }
                    });
                    availableRecyclerView.setAdapter(availableDeviceAdapter);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
                    availableRecyclerView.setLayoutManager(layoutManager);
                    availableDeviceAdapter.addAvailableDevice(availableList);
                }
                else {

                }
            }
        }
    };
}
