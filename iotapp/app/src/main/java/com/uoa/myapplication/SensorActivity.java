package com.uoa.myapplication;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

public class SensorActivity extends AppCompatActivity
        implements SmokeCreate.SmokeSensorCreateListener, GasCreate.GasSensorCreateListener,
        TempCreate.TempSensorCreateListener, UvCreate.UvSensorCreateListener {

    private myViewModel viewModel;
    private String sensorType = "";
    private String sensorMinimum = "";
    private String sensorMaximum = "";
    private static final String[] SENSOR_TYPES = {"smoke", "gas", "temp", "uv"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        createTabs();
    }

    @Override
    public void onCreateSmokeSensor() {
        sensorType = SENSOR_TYPES[0];
        viewModel.getSmokeMinimum().observe(this, value -> sensorMinimum = String.valueOf(value));
        viewModel.getSmokeMaximum().observe(this, value -> sensorMaximum = String.valueOf(value));
        createSensor();
    }

    @Override
    public void onCreateGasSensor() {
        sensorType = SENSOR_TYPES[1];
        viewModel.getGasMinimum().observe(this, value -> sensorMinimum = String.valueOf(value));
        viewModel.getGasMaximum().observe(this, value -> sensorMaximum = String.valueOf(value));
        createSensor();
    }

    @Override
    public void onCreateTempSensor() {
        sensorType = SENSOR_TYPES[2];
        viewModel.getTempMinimum().observe(this, value -> sensorMinimum = String.valueOf(value));
        viewModel.getTempMaximum().observe(this, value -> sensorMaximum = String.valueOf(value));
        createSensor();
    }

    @Override
    public void onCreateUvSensor() {
        sensorType = SENSOR_TYPES[3];
        viewModel.getUvMinimum().observe(this, value -> sensorMinimum = String.valueOf(value));
        viewModel.getUvMaximum().observe(this, value -> sensorMaximum = String.valueOf(value));
        createSensor();
    }

    private void createTabs() {
        ViewPager viewPager = findViewById(R.id.createSensorViewPager);
        TabLayout tabs = findViewById(R.id.createSensorTabs);
        MyAdapter adapter = new MyAdapter(getSupportFragmentManager());
        adapter.addFragment(new SmokeCreate(), getResources().getString(R.string.titleSmoke));
        adapter.addFragment(new GasCreate(), getResources().getString(R.string.titleGas));
        adapter.addFragment(new TempCreate(), getResources().getString(R.string.titleTemp));
        adapter.addFragment(new UvCreate(), getResources().getString(R.string.titleUv));
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
        viewModel = new ViewModelProvider(this).get(myViewModel.class);
    }

    private void createSensor() {
        Intent intent = new Intent();
        intent.putExtra("type", sensorType);
        intent.putExtra("Minimum", sensorMinimum);
        intent.putExtra("Maximum", sensorMaximum);
        intent.putExtra("SliderValue", String.valueOf((Float.parseFloat(sensorMaximum) + Float.parseFloat(sensorMinimum)) / 2));
        setResult(RESULT_OK, intent);
        finish();
    }

}