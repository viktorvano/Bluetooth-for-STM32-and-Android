/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.viktorvano.bmp280.bluetoothle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private double startTime = 0.0;
    private ArrayList<Entry> valuesTemperature = new ArrayList<>();
    private ArrayList<Entry> valuesPressure = new ArrayList<>();
    private ArrayList<Entry> valuesAltitude = new ArrayList<>();
    private Handler handler = new Handler();
    private int updatePeriod = 1000;
    private SeekBar seekBarUpdate, seekBarDataSet;
    private TextView textViewUpdate, textViewDataSet;
    private LineChart chartTemperature, chartPressure, chartAltitude;
    private Switch aSwitchRun;
    private Button buttonClear;
    private int maximumDataSet = 20;

    private String receiveBuffer = "";

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private void messageHandler() {
        if (receiveBuffer != null) {
            double currentTime;
            float temperature = -999.0f, pressure = -999.0f, altitude = -999.0f;

            try
            {
                String[] splits;
                splits = receiveBuffer.split(",");
                temperature = Float.parseFloat(splits[0]);
                pressure = Float.parseFloat(splits[1]);
                altitude = Float.parseFloat(splits[2]);
            }catch (Exception e)
            {
                temperature = -999.0f;
                pressure = -999.0f;
                altitude = -999.0f;
            }

            if(temperature != -999.0 || pressure != -999.0 || altitude != -999.0)
            {
                if(startTime == 0.0)
                {
                    startTime = Calendar.getInstance().getTimeInMillis();
                    currentTime = startTime;
                }else
                {
                    currentTime = Calendar.getInstance().getTimeInMillis();
                }

                double time = (currentTime - startTime) / 1000.0;

                valuesTemperature.add(new Entry((float)time, temperature));
                valuesPressure.add(new Entry((float)time, pressure));
                valuesAltitude.add(new Entry((float)time, altitude));

                while(valuesTemperature.size() > maximumDataSet)
                    valuesTemperature.remove(0);

                while(valuesPressure.size() > maximumDataSet)
                    valuesPressure.remove(0);

                while(valuesAltitude.size() > maximumDataSet)
                    valuesAltitude.remove(0);

                updateCharts();
            }

        }
    }

    private void initializeCharts()
    {
        chartTemperature = findViewById(R.id.chartTemperature);
        chartTemperature.setDrawGridBackground(false);

        // no description text
        chartTemperature.getDescription().setEnabled(false);

        // enable touch gestures
        chartTemperature.setTouchEnabled(false);

        // enable scaling and dragging
        chartTemperature.setDragEnabled(false);
        chartTemperature.setScaleEnabled(false);
        chartTemperature.setScaleY(1.0f);

        // if disabled, scaling can be done on x- and y-axis separately
        chartTemperature.setPinchZoom(false);

        chartTemperature.getAxisLeft().setDrawGridLines(false);
        chartTemperature.getAxisRight().setEnabled(false);
        chartTemperature.getXAxis().setDrawGridLines(false);
        chartTemperature.getXAxis().setDrawAxisLine(false);



        chartPressure = findViewById(R.id.chartPressure);
        chartPressure.setDrawGridBackground(false);

        // no description text
        chartPressure.getDescription().setEnabled(false);

        // enable touch gestures
        chartPressure.setTouchEnabled(false);

        // enable scaling and dragging
        chartPressure.setDragEnabled(false);
        chartPressure.setScaleEnabled(false);
        chartPressure.setScaleY(1.0f);

        // if disabled, scaling can be done on x- and y-axis separately
        chartPressure.setPinchZoom(false);

        chartPressure.getAxisLeft().setDrawGridLines(false);
        chartPressure.getAxisRight().setEnabled(false);
        chartPressure.getXAxis().setDrawGridLines(false);
        chartPressure.getXAxis().setDrawAxisLine(false);



        chartAltitude = findViewById(R.id.chartAltitude);
        chartAltitude.setDrawGridBackground(false);

        // no description text
        chartAltitude.getDescription().setEnabled(false);

        // enable touch gestures
        chartAltitude.setTouchEnabled(false);

        // enable scaling and dragging
        chartAltitude.setDragEnabled(false);
        chartAltitude.setScaleEnabled(false);
        chartAltitude.setScaleY(1.0f);

        // if disabled, scaling can be done on x- and y-axis separately
        chartAltitude.setPinchZoom(false);

        chartAltitude.getAxisLeft().setDrawGridLines(false);
        chartAltitude.getAxisRight().setEnabled(false);
        chartAltitude.getXAxis().setDrawGridLines(false);
        chartAltitude.getXAxis().setDrawAxisLine(false);
    }

    private void updateCharts()
    {
        chartTemperature.resetTracking();
        chartPressure.resetTracking();
        chartAltitude.resetTracking();

        setData();
        // redraw
        chartTemperature.invalidate();
        chartPressure.invalidate();
        chartAltitude.invalidate();
    }

    private void setData() {
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(valuesTemperature, "Temperature");

        set1.setColor(Color.RED);
        set1.setLineWidth(1.0f);
        set1.setDrawValues(false);
        set1.setDrawCircles(false);
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setDrawFilled(false);

        // create a data object with the data sets
        LineData data = new LineData(set1);

        // set data
        chartTemperature.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chartTemperature.getLegend();
        l.setEnabled(true);


        // create a dataset and give it a type
        set1 = new LineDataSet(valuesPressure, "Pressure");

        set1.setColor(Color.GREEN);
        set1.setLineWidth(1.0f);
        set1.setDrawValues(false);
        set1.setDrawCircles(false);
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setDrawFilled(false);

        // create a data object with the data sets
        data = new LineData(set1);

        // set data
        chartPressure.setData(data);

        // get the legend (only possible after setting data)
        l = chartPressure.getLegend();
        l.setEnabled(true);



        // create a dataset and give it a type
        set1 = new LineDataSet(valuesAltitude, "Altitude");

        set1.setColor(Color.BLUE);
        set1.setLineWidth(1.0f);
        set1.setDrawValues(false);
        set1.setDrawCircles(false);
        set1.setMode(LineDataSet.Mode.LINEAR);
        set1.setDrawFilled(false);

        // create a data object with the data sets
        data = new LineData(set1);

        // set data
        chartAltitude.setData(data);

        // get the legend (only possible after setting data)
        l = chartAltitude.getLegend();
        l.setEnabled(true);
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                receiveBuffer += intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if(receiveBuffer.contains("\n")) {
                    receiveBuffer = receiveBuffer.substring(0, receiveBuffer.length() - 1);
                    messageHandler();
                    receiveBuffer = "";
                }
            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
    };

    private void clearUI() {
        receiveBuffer="";
        valuesTemperature.clear();
        valuesPressure.clear();
        valuesAltitude.clear();
        startTime = 0.0;
        updateCharts();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Sets up UI references.
        mConnectionState = findViewById(R.id.connection_state);

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        initializeCharts();

        aSwitchRun = findViewById(R.id.switchRun);

        textViewUpdate = findViewById(R.id.textViewUpdate);

        textViewDataSet = findViewById(R.id.textViewDataSet);
        textViewDataSet.setText("Data set: " + maximumDataSet);

        seekBarUpdate = findViewById(R.id.seekBarUpdate);
        seekBarUpdate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePeriod = (progress + 1) * 1000;
                if(progress != 0)
                    textViewUpdate.setText("Update period: " + (progress + 1) + " seconds");
                else
                    textViewUpdate.setText("Update period: 1 second");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarDataSet = findViewById(R.id.seekBarDataSet);
        seekBarDataSet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maximumDataSet = progress + 20;
                textViewDataSet.setText("Data set: " + (progress + 20));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buttonClear = findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearUI();
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                handler.postDelayed(this, updatePeriod);
                try {
                    if(mConnected && aSwitchRun.isChecked())
                        mBluetoothLeService.writeCharacteristic("get\n");
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, updatePeriod);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
                //custom code
                if(uuid.equals("0000ffe1-0000-1000-8000-00805f9b34fb") && mNotifyCharacteristic == null) {
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
                    mNotifyCharacteristic = gattCharacteristic;
                }
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
