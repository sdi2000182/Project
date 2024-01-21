package com.uoa.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.BatteryManager;
import android.os.Bundle;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.uoa.myapplication.UVFrag;
import com.uoa.myapplication.TempFrag;
import com.uoa.myapplication.SmokeFrag;
import com.uoa.myapplication.GasFrag;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.tabs.TabLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;

import com.uoa.myapplication.FragmentAdapter;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity {

    private Handler handler;
    private MQTTCallBackHandler.CallBackListener listener;
    FragmentAdapter adapter;

    private Connect connect;

    private FloatingActionButton fab;

    List<Sensor> sensors = null;
    private final static String sensorConfigFile = "sensors.json";
    private static final int LOCATION_REQUEST_CODE = 1001;
    private static final int NEW_SENSOR_REQUEST_CODE = 1;
    private static final int LOCATION_PROVIDER_CODE = 2;

    private final AtomicBoolean stopRunnable = new AtomicBoolean(true);
    private final AtomicBoolean gpsPermissionGranted = new AtomicBoolean(false);
    private final AtomicBoolean gpsReady = new AtomicBoolean(false);
    private FusedLocationProviderClient locationProvider;
    private static final String[][] SUDOLOCS = {
            {"37.96809452684323", "23.76630586399502"},
            {"37.96799937191987", "23.766603589104385"},
            {"37.967779456380754", "23.767174897611685"},
            {"37.96790421900921", "23.76626294807113"}
    };
    private String latitude = "";
    private String longitude = "";
    BatteryManager batteryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setUpTheme();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createTabs();

        setUpConnectionFABListener();

        // Create the connection and register the callback listener
        String serverUri = "tcp://" + getResources().getString(R.string.defaultServerIp) + ":" + getResources().getString(R.string.defaultServerPort);
        setupConnectionListener();
        try {
            connect = new Connect(serverUri, getStringfunc("sessionId"), new MemoryPersistence(), this, listener);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        handler = new Handler();
        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                ExitMessage();

            }

        };
        getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settingsOpt) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (item.getItemId() == R.id.createOpt) {
            startActivityForResult(new Intent(this, SensorCreateActivity.class), NEW_SENSOR_REQUEST_CODE);
            return true;
        } else if (item.getItemId() == R.id.exitOpt) {
            Jsonify.putJsonContent(this, sensorConfigFile, sensors);
            ExitMessage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PROVIDER_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpLocationProvider();
            } else {
                stopRunnable.set(true);
                gpsPermissionGranted.set(false);
                Toast.makeText(this, "You need to give location permission to enable Auto Coordinates mode", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_SENSOR_REQUEST_CODE && resultCode == RESULT_OK) {
            Sensor s = new Sensor(data.getStringExtra("type"), Float.parseFloat(data.getStringExtra("min")),
                    Float.parseFloat(data.getStringExtra("max")), Float.parseFloat(data.getStringExtra("current")));
            sensors.add(s);
            refreshUi();
        }
    }

    private void createTabs() {
        adapter = new FragmentAdapter(getSupportFragmentManager());
        if (getStringfunc("sessionId").equals("-1")) {
            // If this is the first run of the app, load the default sensors
            setStringSetting("sessionId", String.valueOf(new Random().nextInt(10000)));
            sensors = Jsonify.getJsonContent(this, sensorConfigFile, true);
            Jsonify.putJsonContent(this, sensorConfigFile, sensors);
        } else {
            // Otherwise load the sensors the app has stored
            sensors = Jsonify.getJsonContent(this, sensorConfigFile, false);
        }
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(sensors.size() - 1);
        TabLayout tabs = findViewById(R.id.tabs);
        int smokeCounter = 0, gasCounter = 0, tempCounter = 0, uvCounter = 0;
        for (int sensor = 0; sensor < sensors.size(); sensor++) {
            Fragment fragment = null;
            Bundle bundle = new Bundle();
            bundle.putFloatArray("args", new float[]{sensors.get(sensor).getMin(), sensors.get(sensor).getMax(), sensors.get(sensor).getCurrent()});
            switch (sensors.get(sensor).getType()) {
                case "smoke":
                    fragment = new SmokeFrag();
                    adapter.addFragment(fragment, getResources().getString(R.string.titleSmoke) + " " + ++smokeCounter);
                    break;
                case "gas":
                    fragment = new GasFrag();
                    adapter.addFragment(fragment, getResources().getString(R.string.titleGas) + " " + ++gasCounter);
                    break;
                case "temp":
                    fragment = new TempFrag();
                    adapter.addFragment(fragment, getResources().getString(R.string.titleTemp) + " " + ++tempCounter);
                    break;
                case "uv":
                    fragment = new UVFrag();
                    adapter.addFragment(fragment, getResources().getString(R.string.titleUv) + " " + ++uvCounter);
                    break;
                default:
                    break;
            }
            if (fragment != null) fragment.setArguments(bundle);
        }
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);
    }

    private void setupConnectionListener() {
        listener = new MQTTCallBackHandler.CallBackListener() {
            @Override
            public void onMessageArrived(String topic, MqttMessage message) {
                handler.post(() -> handleMessageArrived(topic, message));
            }

            @Override
            public void onConnectionLost() {
                handler.post(() -> handleConnectionLost());
            }

            @Override
            public void onReconnected() {
                handler.post(() -> handleReconnected());
            }
        };
    }

    private void toggleConnect() {
        if (connect.isConnected()) disconnect();
        else connect();
    }

    private void connect() {
        try {
            IMqttToken token = connect.connect(connect.getConnectOptions());
            token.waitForCompletion(1000);
            onConnectSuccess();
            // Subscribe by default to all available topic with QoS Exactly Once
            subscribe("#", 2);
        } catch (MqttException e) {
            onConnectFailure();
            e.printStackTrace();
        }
    }

    private void publish(String topic, String payload, int qos, boolean retain) {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        message.setRetained(retain);
        if (connect.isConnected()) {
            turnOnFAB();
            try {
                connect.publish(connect.getPubTopic(), message).waitForCompletion();
                System.out.println("Successfully published \"" + payload + "\" to " + connect.getPubTopic());
                Toast.makeText(this, "Published \"" + payload + "\" to " + topic, Toast.LENGTH_SHORT).show();
            } catch (MqttException e) {
                System.err.println("Failed to publish \"" + payload + "\" to " + connect.getPubTopic());
                e.printStackTrace();
            }
        } else {
            turnOffFAB();
            Toast.makeText(this, "You need to connect first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void subscribe(String topic, int qos) {
        if (connect.isConnected()) {
            turnOnFAB();
            try {
                connect.setSubTopic(topic);
                connect.subscribe(connect.getSubTopic(), qos);
                System.out.println("Successfully subscribed to " + connect.getSubTopic());
                Toast.makeText(this, "Subscribed to " + topic, Toast.LENGTH_SHORT).show();
            } catch (MqttException e) {
                System.err.println("Failed to subscribe to " + connect.getSubTopic());
                e.printStackTrace();
            }
        } else {
            turnOffFAB();
            Toast.makeText(this, "You need to connect first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void unsubscribe(String topic) {
        if (connect.isConnected()) {
            turnOnFAB();
            try {
                connect.unsubscribe(topic);
                System.out.println("Successfully unsubscribed from " + topic);
            } catch (MqttException e) {
                System.err.println("Failed to unsubscribe from " + topic);
                e.printStackTrace();
            }
        } else {
            turnOffFAB();
            Toast.makeText(this, "You need to connect first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void disconnect() {
        try {
            // Unsubscribe from all available topics
            unsubscribe("civil/server-sensors/" + connect.getSessionID() + "/#");
            IMqttToken token = connect.disconnect();
            token.waitForCompletion(1000);
            onDisconnectSuccess();
        } catch (MqttException e) {
            onDisconnectFailure();
            e.printStackTrace();
        }
    }

    // Returns a map of all sensors on the current fragments along with their status
    HashMap<Sensor, Boolean> getCurrentSensors() {
        HashMap<Sensor, Boolean> currentSensors = new HashMap<>();
        for (int sensor = 0; sensor < sensors.size(); sensor++) {
            CheckBox checkBox = null;
            Slider slider = null;
            boolean isActive = false;
            // Double-check if the fragment of this sensor is attached
            if (adapter.getItem(sensor).isAdded()) {
                switch (sensors.get(sensor).getType()) {
                    case "smoke":
                        checkBox = adapter.getItem(sensor).requireView().findViewById(R.id.smokeSensorActiveCheckBox);
                        slider = adapter.getItem(sensor).requireView().findViewById(R.id.smokeSensorSlider);
                        break;
                    case "gas":
                        checkBox = adapter.getItem(sensor).requireView().findViewById(R.id.gasSensorActiveCheckBox);
                        slider = adapter.getItem(sensor).requireView().findViewById(R.id.gasSensorSlider);
                        break;
                    case "temp":
                        checkBox = adapter.getItem(sensor).requireView().findViewById(R.id.tempSensorActiveCheckBox);
                        slider = adapter.getItem(sensor).requireView().findViewById(R.id.tempSensorSlider);
                        break;
                    case "uv":
                        checkBox = adapter.getItem(sensor).requireView().findViewById(R.id.uvCheck);
                        slider = adapter.getItem(sensor).requireView().findViewById(R.id.uvSensorSlider);
                        break;
                    default:
                        break;
                }
            }
            if (checkBox != null) isActive = checkBox.isChecked();
            if (slider != null) sensors.get(sensor).setCurrent(slider.getValue());
            if (checkBox != null && slider != null)
                currentSensors.put(sensors.get(sensor), isActive);
        }
        return currentSensors;
    }

    private void onConnectSuccess() {
        turnOnFAB();
        System.out.println("Success connected to " + connect.getServerUri() + getBoolFunc("auto_coords"));
        Toast.makeText(this, "Connected to " + connect.getServerIp(), Toast.LENGTH_SHORT).show();
        // Check which location mode is selected

        if (getBoolFunc("auto_coords")) {
            // Auto mode connect - Pick coordinates from GPS sensor
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PROVIDER_CODE);
                System.out.println("swstaaa");
            } else {
                System.out.println("set up");
                setUpLocationProvider();
            }
        } else {
            // Manual mode connect - Pick coordinates from preferences
            switch (Integer.parseInt(getStringfunc("location"))) {
                case 0:
                    latitude = SUDOLOCS[0][0];
                    longitude = SUDOLOCS[0][1];
                    break;
                case 1:
                    latitude = SUDOLOCS[1][0];
                    longitude = SUDOLOCS[1][1];
                    break;
                case 2:
                    latitude = SUDOLOCS[2][0];
                    longitude = SUDOLOCS[2][1];
                    break;
                case 3:
                    latitude = SUDOLOCS[3][0];
                    longitude = SUDOLOCS[3][1];
                    break;
            }
            SensorRunnable runnable = new SensorRunnable();
            new Thread(runnable).start();
        }
    }

    private void onConnectFailure() {
        turnOffFAB();
        System.err.println("Failed to connect to " + connect.getServerUri());
        if (connect.isInternetServiceAvailable())
            Toast.makeText(this, "Failed to connect. Please check your IP/port settings and retry!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "Failed to connect. Please check your internet connect and retry!", Toast.LENGTH_SHORT).show();
    }

    private void onDisconnectSuccess() {
        stopRunnable.set(true);
        turnOffFAB();
        System.out.println("Successfully disconnected from " + connect.getServerUri());
        Toast.makeText(this, "Disconnected from " + connect.getServerIp(), Toast.LENGTH_SHORT).show();
    }

    private void onDisconnectFailure() {
        turnOnFAB();
        System.out.println("Failed to disconnect from " + connect.getServerUri());
        Toast.makeText(this, "Failed to disconnect from " + connect.getServerIp(), Toast.LENGTH_SHORT).show();
    }

    private void handleMessageArrived(String topic, MqttMessage message) {
        System.out.println("Received " + message + " in " + topic);
        Toast.makeText(connect.getContext(), "Received " + message + " in " + topic, Toast.LENGTH_SHORT).show();
    }

    private void handleConnectionLost() {
        turnOffFAB();
        System.out.println("Connection was lost!");
        Toast.makeText(this, "Connection was lost!", Toast.LENGTH_SHORT).show();
    }

    private void handleReconnected() {
        turnOnFAB();
        System.out.println("Reconnected Successfully!");
        Toast.makeText(this, "Reconnected Successfully!", Toast.LENGTH_SHORT).show();
    }


    private void setUpLocationProvider() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            System.out.println("oookkk");
            return;
        }

        // Create a location request
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set up the location callback
        FusedLocationProviderClient locationProvider = LocationServices.getFusedLocationProviderClient(this);
        locationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        // Get the last known location immediately
        locationProvider.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                System.out.println("not null");
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Longitude", longitude);
                editor.putString("Latitude", latitude);
                editor.apply();

                gpsReady.set(true);
                System.out.println("setting gps Per");
                gpsPermissionGranted.set(true);
                SensorRunnable runnable = new SensorRunnable();
                new Thread(runnable).start();
            }
            System.out.println("naye0onnull");
        });
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            System.out.println("momonull");
            if (locationResult != null && locationResult.getLastLocation() != null) {
                // Update the values in SharedPreferences only if needed
                System.out.println("jihyonnull");
                if (!gpsReady.get()) {
                    System.out.println("oknull");
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    String longitude = String.valueOf(locationResult.getLastLocation().getLongitude());
                    String latitude = String.valueOf(locationResult.getLastLocation().getLatitude());
                    editor.putString("Longitude", longitude);
                    editor.putString("Latitude", latitude);
                }
            }
        }
    };

//    private void setUpTheme() {
//
//            try {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//    }

    private void setUpConnectionFABListener() {
        fab = findViewById(R.id.fab);
        turnOffFAB();
        fab.setOnClickListener(v -> toggleConnect());
    }

    private void turnOnFAB() {
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.teal_700)));
        fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_tethering_on, null));
    }

    private void turnOffFAB() {
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow_200)));
        fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_tethering_off, null));
    }

    private void refreshUi() {
        Jsonify.putJsonContent(this, sensorConfigFile, sensors);
        finish();
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }


    public String getStringfunc(String key) {
        return getDefaultSharedPreferences(this).getString(key, "-1");
    }


    public Boolean getBoolFunc(String key) {
        return getDefaultSharedPreferences(this).getBoolean(key, false);
    }

    public void setStringSetting(String key, String value) {
        getDefaultSharedPreferences(this).edit().putString(key, value).apply();
    }


    private String getBatteryLevel() {
        return String.valueOf(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
    }

    private void ExitMessage() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to exit the application?");
        builder.setMessage("");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> finish());
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }



    class SensorRunnable implements Runnable {
        @Override
        public void run() {
            stopRunnable.set(false);

            ScheduledExecutorService service = Executors.newScheduledThreadPool(0);
            ScheduledFuture<?> serviceHandler = service.scheduleAtFixedRate(() -> {
                if (stopRunnable.get()) {
                    service.shutdownNow();
                } else {
                    System.out.println("gps1 " + gpsPermissionGranted.get() + " gps2 " + gpsReady.get());
                    if (!getBoolFunc("auto_coords") || (gpsPermissionGranted.get() && gpsReady.get())) {
                        HashMap<Sensor, Boolean> sensors = getCurrentSensors();
                        StringBuilder payload = new StringBuilder();
                        payload.append("{\n")
                                .append("\"latitude\": ").append(latitude).append(",\n")
                                .append("\"longitude\": ").append(longitude).append(",\n")
                                .append("\"battery\": ").append(getBatteryLevel()).append(",\n");
                        // Only append data of currently active sensors
                        for (Map.Entry<Sensor, Boolean> entry : sensors.entrySet())
                            if (entry.getValue().toString().equals("true"))
                                payload.append("\"").append(entry.getKey().getType()).append("\": ").append(entry.getKey().getCurrent()).append(",\n");
                        payload.deleteCharAt(payload.length() - 2);
                        payload.append("} ");
                        connect.setPubTopic("data");
                        connect.setMessage(payload.toString().substring(0, payload.toString().length() - 1));
                        // Assign job to Main thread
                        handler.post(() -> publish(connect.getPubTopic(), connect.getMessage(), connect.getQos(), connect.isRetain()));
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }


}