package com.example.home.impactlogger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements AdapterView.OnItemClickListener {

    ArrayAdapter<String> mDeviceAdapter;
    Button scanButton;
    ListView listView;
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> devicesArray;
    ArrayList<String> pairedDevices;
//    public static final UUID MY_UUID = UUID.fromString("fe84-0000-1000-8000-00805f9b34fb");
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    protected static final int SUCCESS_CONNECT = 0;
    IntentFilter filter;
    BroadcastReceiver receiver;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SUCCESS_CONNECT:
                    Toast.makeText(getActivity(), "Connected", Toast.LENGTH_LONG).show();


            }
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        listView = (ListView) rootView.findViewById(R.id.listview_devices);
        scanButton = (Button) rootView.findViewById(R.id.bConnectionNew);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
            }
        });

        mDeviceAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_device,
                R.id.list_item_device_textview);
        listView.setAdapter(mDeviceAdapter);
        listView.setOnItemClickListener(this);
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();


//        btAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<String>();
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    String s = "";
                    for (int a = 0; a < pairedDevices.size(); a++) {
                        if (device.getName().equals(pairedDevices.get(a))) {
                            s = "(PAIRED)";
                            break;
                        }
                    }
                    mDeviceAdapter.add(device.getName() + s + "\n" + device.getAddress());
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {


                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    if (btAdapter.getState() == btAdapter.STATE_OFF) {
                        turnOnBt();
                    }

                }

            }

        };

        if (btAdapter == null) {
            Toast.makeText(getActivity(), "No Bluetooth Detected", Toast.LENGTH_LONG).show();
        } else {
            if (!btAdapter.isEnabled()) {
                turnOnBt();
            }
            getPairedDevices();
            startDiscovery();
        }

        return rootView;

    }

    private void startDiscovery() {
        mDeviceAdapter.clear();
        btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();

    }

    private void turnOnBt() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        getActivity().registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(receiver, filter);

    }

    private void getPairedDevices() {
        devicesArray = btAdapter.getBondedDevices();
        if (devicesArray.size() > 0) {
            for (BluetoothDevice device : devicesArray) {
                pairedDevices.add(device.getName());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        }

        if (mDeviceAdapter.getItem(position).contains("PAIRED")) {
            BluetoothDevice selectedDevice = (BluetoothDevice)devicesArray.toArray()[position];
            ConnectThread connect = new ConnectThread(selectedDevice);
            connect.start();
        } else {
            BluetoothDevice selectedDevice = (BluetoothDevice)devicesArray.toArray()[position];
            ConnectThread connect = new ConnectThread(selectedDevice);
            connect.start();        }
        Intent otherIntent = new Intent(getActivity(), ImpactActivity.class)
                .putExtra(Intent.EXTRA_TEXT, mDeviceAdapter.getItem(position));
        startActivity(otherIntent);

    }
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            btAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
            mHandler.obtainMessage(SUCCESS_CONNECT);
        }
        public void manageConnectedSocket(BluetoothSocket mmSocket){

        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
