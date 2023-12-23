package com.uoa.sensor2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.tabs.TabLayoutMediator;
import com.uoa.sensor2.MyAdapter;
import com.uoa.sensor2.GasCreate;
import com.uoa.sensor2.SmokeCreate;
import com.uoa.sensor2.TempCreate;
import com.uoa.sensor2.UVcreate;
import com.uoa.sensor2.ViewModel1;
import com.google.android.material.tabs.TabLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import java.util.concurrent.TimeUnit;

public class SensorActivity extends AppCompatActivity
        implements SmokeCreate.SmokeSensorListener, GasCreate.GasSensorListener,
        TempCreate.TempSensorListener, UVcreate.UVSensorListener {



    private ViewModel1 viewModel;
    private String sensorType = "";
    private String Minimum = "";
    private String Maximum = "";
    private static final String[] SENSOR_TYPES = {"Smoke", "Gas", "Temp", "UV"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("I have entered onCreate\n");
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Code to execute after the delay (e.g., other tasks)
//
//                // For example, performing additional actions after the delay
//                // This is where you can continue with other operations
//                // ...
//
//                // For demonstration purposes, let's print a message
//                System.out.println("Continuing meow the delay...");
//
//                // ... (Other tasks to perform after the delay)
//            }
//        }, 7000);
        setContentView(R.layout.activity_sensor);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Code to execute after the delay (e.g., other tasks)
//
//                // For example, performing additional actions after the delay
//                // This is where you can continue with other operations
//                // ...
//
//                // For demonstration purposes, let's print a message
//                System.out.println("after the delay...");
//
//                // ... (Other tasks to perform after the delay)
//            }
        //}, 7000);
        System.out.println("I have set view\n");
        createTabs();
    }

    @Override
    public void onCreateSmokeSensor() {
        sensorType = SENSOR_TYPES[0];
        viewModel.getSmokeMinimum().observe(this, value -> Minimum = String.valueOf(value));
        viewModel.getSmokeMaximum().observe(this, value -> Maximum = String.valueOf(value));
        createSensor();
    }


    @Override
    public void onCreateGasSensor() {
        sensorType = SENSOR_TYPES[1];
        viewModel.getGasMinimum().observe(this, value -> Minimum = String.valueOf(value));
        viewModel.getGasMaximum().observe(this, value -> Maximum = String.valueOf(value));
        createSensor();
    }

    @Override
    public void onCreateTempSensor() {
        sensorType = SENSOR_TYPES[2];
        viewModel.getTempMinimum().observe(this, value -> Minimum = String.valueOf(value));
        viewModel.getTempMaximum().observe(this, value -> Maximum = String.valueOf(value));
        createSensor();
    }

    @Override
    public void onCreateUVSensor() {
        sensorType = SENSOR_TYPES[3];
        viewModel.getUVMinimum().observe(this, value -> Minimum = String.valueOf(value));
        viewModel.getUVMaximum().observe(this, value -> Maximum = String.valueOf(value));
        createSensor();
    }

    private void createTabs() {
        ViewPager2 viewPager = findViewById(R.id.createSensorViewPager);
        System.out.println("I have created viewpager\n");
//        TabLayout tabs = findViewById(R.id.createSensorTabs);
        TabLayout tabLayout = findViewById(R.id.createSensorTabs);


        System.out.println("I have created tabs\n");
//        MyAdapter adapter = new MyAdapter(getSupportFragmentManager());
        MyAdapter adapter = new MyAdapter(SensorActivity.this);
        System.out.println("I have created the thingy\n");
        adapter.addFragment(new SmokeCreate(), getResources().getString(R.string.tabSmoke));
        adapter.addFragment(new GasCreate(), getResources().getString(R.string.tabGas));
        adapter.addFragment(new TempCreate(), getResources().getString(R.string.tabTemp));
        adapter.addFragment(new UVcreate(), getResources().getString(R.string.tabUV));
        viewPager.setAdapter(adapter);
//        tabs.setupWithViewPager(viewPager);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(adapter.getTitleList((position)))
        ).attach();
        viewModel = new ViewModelProvider(this).get(ViewModel1.class);
        System.out.println("I have finished tabs\n");



    }

    private void createSensor() {
        System.out.println("I have entered create Sensor\n");
        Intent intent = new Intent();
        intent.putExtra("Maximum", Maximum);
        intent.putExtra("SensorType", sensorType);

        intent.putExtra("Minimum", Minimum);
        intent.putExtra("Maximum", Maximum);
        intent.putExtra("SliderValue", String.valueOf((Float.parseFloat(Maximum) + Float.parseFloat(Minimum)) / 2));
        setResult(RESULT_OK, intent);
        finish();
    }

}