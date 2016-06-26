package com.example.home.impactlogger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    ArrayAdapter<String> mDeviceAdapter;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        String[] deviceArray = {
                "John",
                "Max",
                "Kevin",
                "Yash"
        };
        List<String> deviceList = new ArrayList<String>(Arrays.asList(deviceArray));
        //ArrayAdapter
        mDeviceAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_device,
                R.id.list_item_device_textview,
                deviceArray
        );
        //get refernce to the listview and attach this adapter
        ListView listView = (ListView)rootView.findViewById(
                R.id.listview_devices);
        listView.setAdapter(mDeviceAdapter);
        return rootView;
    }
}
