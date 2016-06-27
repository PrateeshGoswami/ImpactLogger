package com.example.home.impactlogger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    ArrayAdapter<String> mDeviceAdapter;
    Button connectNew;
    ListView listView;
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> devicesArray;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//        String[] deviceArray = {
//                "John",
//                "Max",
//                "Kevin",
//                "Yash"
//        };
//        List<String> deviceList = new ArrayList<String>(Arrays.asList(deviceArray));
//        //ArrayAdapter
//        mDeviceAdapter = new ArrayAdapter<String>(
//                getActivity(),
//                R.layout.list_item_device,
//                R.id.list_item_device_textview,
//                deviceArray
//        );
//        //get refernce to the listview and attach this adapter
//        listView = (ListView)rootView.findViewById(
//                R.id.listview_devices);
//        listView.setAdapter(mDeviceAdapter);
        connectNew = (Button) rootView.findViewById(R.id.bConnectionNew);
        listView = (ListView) rootView.findViewById(R.id.listview_devices);
        mDeviceAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_device,
                R.id.list_item_device_textview);
        listView.setAdapter(mDeviceAdapter);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter==null){
            Toast.makeText(getActivity(),"No Bluetooth Detected",Toast.LENGTH_LONG).show();
               }else {
            if(!btAdapter.isEnabled()){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent,1);
            }
            getPairedDevices();
        }

        return rootView;

    }

    private void getPairedDevices() {
        devicesArray = btAdapter.getBondedDevices();
        if (devicesArray.size()>0){
            for(BluetoothDevice device:devicesArray){
            mDeviceAdapter.add(device.getName()+"\n"+device.getAddress());
            }
        }
    }
}
