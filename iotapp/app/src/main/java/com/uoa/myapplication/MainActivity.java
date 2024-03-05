package com.uoa.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.BatteryManager;
import android.os.Bundle;
import com.google.android.gms.location.LocationRequest;

import androidx.preference.PreferenceManager;
import androidx.activity.OnBackPressedCallback;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.tabs.TabLayout;

import android.os.Looper;
import android.provider.Settings.Secure;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
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
    MyAdapter adapter;

    private Connect myConnection;
    private MQTTCallBackHandler.CallBackListener listener;

    private FloatingActionButton fab;

    List<Sensor> sensors = null;
    private final static String sensorConfigFile = "sensors.json";
//    private AppDatabase appDatabase;

    private final AtomicBoolean stopRunnable = new AtomicBoolean(true);
    private final AtomicBoolean gpsPermissionGranted = new AtomicBoolean(false);
    private final AtomicBoolean gpsReady = new AtomicBoolean(false);

    private static final int NEW_SENSOR_REQUEST_CODE = 1;
    private static final int LOCATION_PROVIDER_CODE = 2;


    private FusedLocationProviderClient locationProvider;

    private static final String[][] SUDO_LOC = {
        {"37.96809452684323", "23.76630586399502"},
        {"37.96799937191987", "23.766603589104385"},
        {"37.967779456380754", "23.767174897611685"},
        {"37.96790421900921", "23.76626294807113"}
    };
    private String latitude = SUDO_LOC[0][0];
    private SharedPreferences pref;
    private static final String DEVICE_COUNT_KEY = "deviceCount";
    private SharedPreferences.Editor editor;
    private Context context;
    private String longitude = SUDO_LOC[0][1];
    BatteryManager batteryManager;
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_DEVICE_IDS = "device_ids";
//    private String android_id = Secure.getString(context.getContentResolver(),
//            Secure.ANDROID_ID);
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String DEVICE_IDS_KEY = "deviceIds";
    private static final String DEVICE_COUNT_KEY_PREFIX = "deviceCount_";

    private List<String> deviceIds;
    private static final int LOCATION_REQUEST_CODE = 1001;

    private SharedPreferences prefs;

    private static final String PREF_LAUNCH_COUNT = "launchCount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createTabs();
//        incrementLaunchCount();
//        System.out.println("this " + getLaunchCount(getApplicationContext()));
        setUpConnectionFABListener();
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String message = "hello";
//        String directoryPath = System.getProperty("user.dir");

        SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int counter = sharedPrefs.getInt("counter", 0);

// Increment the counter
        counter++;
// Save the updated counter value to SharedPreferences
        sharedPrefs.edit().putInt("counter", counter).apply();
        Toast.makeText(this, "Counter: " + counter, Toast.LENGTH_SHORT).show();

        String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
//        appDatabase = Room.databaseBuilder(getApplicationContext(),
//                AppDatabase.class, "device-database").build();


//        AtomicInteger val= new AtomicInteger(512);
//        insertDevice(androidId, count -> {
//            // Use the inserted count here
//            System.out.println("Inserted device count: " + count);
//            DeviceDao deviceDao = appDatabase.deviceDao();
//            List<Device> allDevices = deviceDao.getAllDevices();
//            for (Device device : allDevices) {
//                System.out.println("Device ID: " + device.getDeviceId());
//                System.out.println("Device Name: " + device.getDeviceCount());
//                // Print other fields as needed
//            }
//            Device existingDevice = appDatabase.deviceDao().findByAndroidId(androidId);
//            val.set(existingDevice.getDeviceCount());
//            System.out.println("valcount: " + val);
//        });


        // Create the connection and register the callback listener
        String serverUri = "tcp://" + getResources().getString(R.string.defaultServerIp) + ":" + getResources().getString(R.string.defaultServerPort);
        setupConnectionListener();
        try {
            myConnection = new Connect(serverUri, getStringFunc("sessionId"), new MemoryPersistence(), this, listener);
        } catch (MqttException e) {
            e.printStackTrace();
        }
//        try {
//            Connection connecmysql = MySQLConnection.getConnection();
////            // Now you can use the 'connection' object to interact with your MySQL database
////            // For example, execute queries, update data, etc.
////            // Remember to close the connection when you're done with it.
////            connection.close();
//        } catch (SQLException e) {
//            System.out.println("i wanna know i wanna know");
//            e.printStackTrace();
//            // Handle SQLException appropriately, e.g., show an error message to the user.
//        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Load the MySQL JDBC driver class
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return; // Exit the method if driver class is not found
                }

                // Assuming you have a query to insert a new row into the devicetable
                String query = "INSERT INTO devicetable (deviceId) VALUES (?)";
                final String URL = "jdbc:mysql://127.0.0.1:3306/devices?user=root";

                try (Connection connectionSQL = DriverManager.getConnection(URL, "root", "theo");
                     PreparedStatement preparedStatement = connectionSQL.prepareStatement(query)) {
                    // Set deviceId for the prepared statement
                    preparedStatement.setInt(1, 5);  // Assuming deviceId is of type INT

                    // Execute the update
                    int rowsAffected = preparedStatement.executeUpdate();
                    System.out.println("Rows affected: " + rowsAffected);
                } catch (SQLException e) {
                    // Handle any SQL exceptions
                    e.printStackTrace();
                }
            }

        });

        thread.start();

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

    public void PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
//    private void insertDevice(String androidId, Consumer<Integer> callback) {
//        new Thread(() -> {
//            Device existingDevice = appDatabase.deviceDao().findByAndroidId(androidId);
//            if (existingDevice == null) {
//                System.out.println("dn yparxo" +androidId);
//                // Android ID doesn't exist in the database, so insert it
//                Device newDevice = new Device(androidId);
//                appDatabase.deviceDao().insert(newDevice);
//
//                // Retrieve the count of devices after insertion
//                int insertedDeviceCount = newDevice.getDeviceCount();
//                callback.accept(insertedDeviceCount);
//            } else {
//                // Android ID already exists in the database
//                // Handle accordingly
//                System.out.println("yparxo" +androidId + existingDevice.getDeviceId() + existingDevice.getDeviceCount());
//                int existingDeviceCount = existingDevice.getDeviceCount();
//                callback.accept(existingDeviceCount);
//            }
//        }).start();
//    }


    public void addDeviceId(String deviceId) {
        String deviceIds = pref.getString(KEY_DEVICE_IDS, "");
        if (!deviceIds.contains(deviceId)) {
            deviceIds += deviceId + ",";
            editor.putString(KEY_DEVICE_IDS, deviceIds);
            editor.putInt(deviceId + PREF_LAUNCH_COUNT, 1); // Initialize launch count to 1
            editor.apply();
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.settingsOpt) {
            startActivity(new Intent(this, MySettings.class));
            return true;
        } else if (itemId == R.id.createOpt) {
            Intent intent = new Intent(this, SensorActivity.class);
            ArrayList<String> sensorTypes = new ArrayList<>();

            for (Sensor sensor : sensors) {
                sensorTypes.add(sensor.getType());
            }

            intent.putStringArrayListExtra("sensorTypes", sensorTypes);
            startActivityForResult(intent, NEW_SENSOR_REQUEST_CODE);
            return true;
        }else if (itemId == R.id.exitOpt) {
            Jsonify.saveSensorData(this, sensorConfigFile, sensors);
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
            Sensor s = new Sensor(data.getStringExtra("type"), Float.parseFloat(data.getStringExtra("Minimum")),
                    Float.parseFloat(data.getStringExtra("Maximum")), Float.parseFloat(data.getStringExtra("SliderValue")));
            sensors.add(s);
            refreshUi();
        }
    }

    private void createTabs() {
        adapter = new MyAdapter(getSupportFragmentManager());
        if (getStringFunc("sessionId").equals("-1")) {
            // If this is the first run of the app, load the default sensors
            setStringFunc("sessionId", String.valueOf(new Random().nextInt(10000)));
            sensors = Jsonify.retrieveSensorData(this, sensorConfigFile, true);
            Jsonify.saveSensorData(this, sensorConfigFile, sensors);
        } else {
            // Otherwise load the sensors the app has stored
            sensors = Jsonify.retrieveSensorData(this, sensorConfigFile, false);
        }
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(sensors.size() - 1);
        TabLayout tabs = findViewById(R.id.tabs);
        int smokeCounter = 0, gasCounter = 0, tempCounter = 0, uvCounter = 0;
        for (int sensor = 0; sensor < sensors.size(); sensor++) {
            Fragment fragment = null;
            Bundle bundle = new Bundle();
            bundle.putFloatArray("args", new float[]{sensors.get(sensor).getMinimum(), sensors.get(sensor).getMaximum(), sensors.get(sensor).getSliderValue()});
            switch (sensors.get(sensor).getType()) {
                case "smoke":
                    fragment = new SmokeFrag();
                    adapter.addFragment(fragment, getResources().getString(R.string.titleSmoke));
                    break;
                case "gas":
                    fragment = new GasFrag();
                    adapter.addFragment(fragment, getResources().getString(R.string.titleGas) + " " + ++gasCounter);
                    break;
                case "temp":
                    fragment = new TempFrag();
                    adapter.addFragment(fragment, getResources().getString(R.string.titleTemp) );
                    break;
                case "uv":
                    fragment = new UVFrag();
                    adapter.addFragment(fragment, getResources().getString(R.string.titleUv));
                    break;
                default:
                    break;
            }
            if (fragment != null) fragment.setArguments(bundle);
        }
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);

    }

    private void toggleConnect() {
        if (myConnection.isConnected()) disconnect();
        else connect();
    }

    private void connect() {
        try {
            IMqttToken token = myConnection.connect(myConnection.getConnectOptions());
            token.waitForCompletion(1000);
            onSuccess();
            // Subscribe by default to all available topic with QoS Exactly Once
            subscribe("#", 2);
        } catch (MqttException e) {
            onFail();
            e.printStackTrace();
        }
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


    private void publish(String topic, String payload, int qos, boolean retain) {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        message.setRetained(retain);
        if (myConnection.isConnected()) {
            tetherOn();
            try {
                myConnection.publish(myConnection.getPubTopic(), message).waitForCompletion();
                System.out.println("Successfully published  to " + myConnection.getPubTopic());
                Toast.makeText(this, "Published  to " + topic, Toast.LENGTH_SHORT).show();
            } catch (MqttException e) {
                System.err.println("Failed to publish \"" + payload + "\" to " + myConnection.getPubTopic());
                e.printStackTrace();
            }
        } else {
            tetherOff();
            Toast.makeText(this, "You need to connect first!", Toast.LENGTH_SHORT).show();
        }
    }



    private void disconnect() {
        try {
            // Unsubscribe from all available topics
            unsubscribe("serverstopics/" + myConnection.getSessionID() + "/#");
            IMqttToken token = myConnection.disconnect();
            token.waitForCompletion(1000);
            hasDisconnected();
        } catch (MqttException e) {
            DisconnectionFail();
            e.printStackTrace();
        }
    }

    public int getLaunchCount(String deviceId) {
        return pref.getInt(deviceId + PREF_LAUNCH_COUNT, 0);
    }
    private void subscribe(String topic, int qos) {
        if (myConnection.isConnected()) {
            tetherOn();
            try {
                myConnection.setSubTopic(topic);
                myConnection.subscribe(myConnection.getSubTopic(), qos);
                Toast.makeText(this, "Subscribed to " + topic, Toast.LENGTH_SHORT).show();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            tetherOff();
            Toast.makeText(this, "You need to connect first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void unsubscribe(String topic) {
        if (myConnection.isConnected()) {
            tetherOn();
            try {
                myConnection.unsubscribe(topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            tetherOff();
            Toast.makeText(this, "You need to connect first!", Toast.LENGTH_SHORT).show();
        }
    }
    public void incrementLaunchCount(String deviceId) {
        int launchCount = getLaunchCount(deviceId);
        editor.putInt(deviceId + PREF_LAUNCH_COUNT, launchCount + 1);
        editor.apply();
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
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Longitude", longitude);
                editor.putString("Latitude", latitude);
                editor.apply();

                gpsReady.set(true);
                gpsPermissionGranted.set(true);
                SensorRunnable runnable = new SensorRunnable();
                new Thread(runnable).start();
            }
        });
    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                if (!gpsReady.get()) {
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

    HashMap<Sensor, Boolean> getAllSensors() {
        HashMap<Sensor, Boolean> sensorStatusMap = new HashMap<>();
        for (int index = 0; index < sensors.size(); index++) {
            CheckBox checkBox = null;
            Slider slider = null;
            boolean isActive = false;
            if (adapter.getItem(index).isAdded()) {
                switch (sensors.get(index).getType()) {
                    case "temp":
                        checkBox = adapter.getItem(index).requireView().findViewById(R.id.tempSensorActiveCheckBox);
                        slider = adapter.getItem(index).requireView().findViewById(R.id.tempSensorSlider);
                        break;
                    case "uv":
                        checkBox = adapter.getItem(index).requireView().findViewById(R.id.uvCheck);
                        slider = adapter.getItem(index).requireView().findViewById(R.id.uvSensorSlider);
                        break;
                    case "smoke":
                        checkBox = adapter.getItem(index).requireView().findViewById(R.id.smokeSensorActiveCheckBox);
                        slider = adapter.getItem(index).requireView().findViewById(R.id.smokeSensorSlider);
                        break;
                    case "gas":
                        checkBox = adapter.getItem(index).requireView().findViewById(R.id.gasSensorActiveCheckBox);
                        slider = adapter.getItem(index).requireView().findViewById(R.id.gasSensorSlider);
                        break;
                    default:
                        break;
                }
            }
            if (checkBox != null) {
                isActive = checkBox.isChecked();
            }
            if (slider != null) {
                sensors.get(index).setSliderValue(slider.getValue());
            }
            if (checkBox != null && slider != null) {
                sensorStatusMap.put(sensors.get(index), isActive);
            }
        }
        return sensorStatusMap;
    }


    private void onSuccess() {
        tetherOn();
        Toast.makeText(this, "Connected to " + myConnection.getServerIp(), Toast.LENGTH_SHORT).show();
        if (getBoolFunc("autoCoords")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PROVIDER_CODE);
            } else {
                setUpLocationProvider();
            }
        } else {
            // Manual mode connection - Pick coordinates from preferences
            switch (Integer.parseInt(getStringFunc("location"))) {
                case 0:
                    latitude = SUDO_LOC[0][0];
                    longitude = SUDO_LOC[0][1];
                    break;
                case 1:
                    latitude = SUDO_LOC[1][0];
                    longitude = SUDO_LOC[1][1];
                    break;
                case 2:
                    latitude = SUDO_LOC[2][0];
                    longitude = SUDO_LOC[2][1];
                    break;
                case 3:
                    latitude = SUDO_LOC[3][0];
                    longitude = SUDO_LOC[3][1];
                    break;
            }
            SensorRunnable runnable = new SensorRunnable();
            new Thread(runnable).start();
        }
    }

    private void onFail() {
        tetherOff();
        if (myConnection.isInternetServiceAvailable())
            Toast.makeText(this, "Failed to connect. Please check your IP/port settings and retry!", Toast.LENGTH_LONG).show();
        else Toast.makeText(this, "Failed to connect. Please check your internet connection and retry!", Toast.LENGTH_SHORT).show();
    }

    private void hasDisconnected() {
        stopRunnable.set(true);
        tetherOff();
        Toast.makeText(this, "Disconnected from " + myConnection.getServerIp(), Toast.LENGTH_SHORT).show();
    }

    private void DisconnectionFail() {
        tetherOn();
        Toast.makeText(this, "Failed to disconnect from " + myConnection.getServerIp(), Toast.LENGTH_SHORT).show();
    }

    private void handleMessageArrived(String topic, MqttMessage message) {
        Toast.makeText(myConnection.getContext(), "Received " + message + " in " + topic, Toast.LENGTH_SHORT).show();
    }

    private void handleConnectionLost() {
        tetherOff();
        Toast.makeText(this, "Connection was lost!", Toast.LENGTH_SHORT).show();
    }

    private void handleReconnected() {
        tetherOn();
        Toast.makeText(this, "Reconnected Successfully!", Toast.LENGTH_SHORT).show();
    }
    
//    @SuppressLint("MissingPermission")
//    private void setUpLocationProvider() {
//        locationProvider.getLastLocation().addOnSuccessListener(this, location -> {
//            if (location != null) {
//                latitude = String.valueOf(location.getLatitude());
//                longitude = String.valueOf(location.getLongitude());
//                gpsReady.set(true);
//                gpsPermissionGranted.set(true);
//                SensorRunnable runnable = new SensorRunnable();
//                new Thread(runnable).start();
//            }
//        });
//    }


    private void setUpConnectionFABListener() {
        fab = findViewById(R.id.fab);
        tetherOff();
        fab.setOnClickListener(v -> toggleConnect());
    }

    private void tetherOn() {
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange_light)));
        fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_tethering_on, null));
    }

    private void tetherOff() {
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.greyish)));
        fab.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_tethering_off, null));
    }

    private void refreshUi() {
        Jsonify.saveSensorData(this, sensorConfigFile, sensors);
        finish();
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    // Retrieves value of string-type setting "key"
    public String getStringFunc(String key) {
        return getDefaultSharedPreferences(this).getString(key, "-1");
    }

    // Retrieves value of int-type setting "key"
    public Integer getIntFunc(String key) {
        return getDefaultSharedPreferences(this).getInt(key, -1);
    }

    // Retrieves value of switch-type setting "key"
    public Boolean getBoolFunc(String key) {
        return getDefaultSharedPreferences(this).getBoolean(key, false);
    }

    // Sets the "key" string setting to the desired "value"
    public void setStringFunc(String key, String value) {
        getDefaultSharedPreferences(this).edit().putString(key, value).apply();
    }

    // Sets the "key" int setting to the desired "value"
    public void setIntFunc(String key, Integer value) {
        getDefaultSharedPreferences(this).edit().putInt(key, value).apply();
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
                    if (!getBoolFunc("autoCoords") || (gpsPermissionGranted.get() && gpsReady.get())) {
                        HashMap<Sensor, Boolean> sensors = getAllSensors();
                        StringBuilder payload = new StringBuilder();
                        payload.append("{\n")
                                .append("\"latitude\": ").append(latitude).append(",\n")
                                .append("\"longitude\": ").append(longitude).append(",\n")
                                .append("\"battery\": ").append(getBatteryLevel()).append(",\n");
                        for (Map.Entry<Sensor, Boolean> entry : sensors.entrySet())
                            if (entry.getValue().toString().equals("true"))
                                payload.append("\"").append(entry.getKey().getType()).append("\": ").append(entry.getKey().getSliderValue()).append(",\n");
                        payload.deleteCharAt(payload.length() - 2);
                        payload.append("} ");
                        myConnection.setPubTopic("data");
                        myConnection.setMessage(payload.toString().substring(0, payload.toString().length() - 1));
                        handler.post(() -> publish(myConnection.getPubTopic(), myConnection.getMessage(), myConnection.getQos(), myConnection.isRetain()));
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }


    // Sets the "key" switch setting to the desired "value"
    public void setBoolFunc(String key, Boolean value) {
        getDefaultSharedPreferences(this).edit().putBoolean(key, value).apply();
    }

    private String getBatteryLevel() {
        return String.valueOf(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
    }

    private void ExitMessage() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want certainly to exit the application?");
        builder.setMessage("");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> finish());
        builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }



//    @Database(entities = {Device.class}, version = 1)
//    public abstract static class AppDatabase extends RoomDatabase {
//
//        public abstract DeviceDao deviceDao();
//    }
}