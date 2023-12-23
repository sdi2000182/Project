package com.uoa.sensor2;
import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.eclipse.paho.client.mqttv3.IMqttToken;
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
import androidx.activity.OnBackPressedCallback;
import androidx.viewpager2.widget.ViewPager2;
import android.Manifest;

public class MainActivity extends AppCompatActivity {

    Button button;

    TextView textView;
    private MQTTCallbackHandler.MQTTCallbackListener MQCL;
    MyAdapter myadapter;
    private ToggleButton  toggle;
    private ActivityResultLauncher<Intent> sensorActivityResultLauncher; //startactivity deprec
    List<Sensor> listOfSensors = null;
    private Handler handler;
    private final static String jsonFile = "IOT.json";
    private Connect connect;
//    private static final int SENSORCODE = 1;
//    private static final int LOCATION_PROVIDER_CODE = 2;

      private final AtomicBoolean forceExit = new AtomicBoolean(true);
      private final AtomicBoolean autoCoordinatesPerm = new AtomicBoolean(false);
//    private final AtomicBoolean gpsReady = new AtomicBoolean(false);
//    private FusedLocationProviderClient locationProvider;
      private static final String[][] SUDOLOC = {
        {"37.96809452684323", "23.76630586399502"},
        {"37.96799937191987", "23.766603589104385"},
        {"37.967779456380754", "23.767174897611685"},
        {"37.96790421900921", "23.76626294807113"}
};
//    private static final String  SENSORCODE = "1";
    private static final int LOCATION_PROVIDER_CODE = 2;

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o!= null && o.getResultCode() == RESULT_OK){
                        if(o.getData()!=null ){
                            Sensor s = new Sensor(o.getData().getStringExtra("SensorType"), Float.parseFloat(o.getData().getStringExtra("Minimum")),
                        Float.parseFloat(o.getData().getStringExtra("Maximum")), Float.parseFloat(o.getData().getStringExtra("SliderValue")));
                        listOfSensors.add(s);
                        refreshApp();
                        }
                    }
                }
            });
    private final AtomicBoolean gpsReady = new AtomicBoolean(false);
    private FusedLocationProviderClient locationProvider;

    BatteryManager batteryManager;
      private String latitude="";
      private String longitude="";
//    BatteryManager batteryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Inflate the activity_main.xml layout
        //Toolbar tb = findViewById((R.id.toolbar));
        toggle = findViewById((R.id.toggleButton));

//        sensorActivityResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        // Handle the result here if needed
//                        Intent data = result.getData();
//                        // Process the data returned from SensorActivity
//                    } else {
//                        // Handle other scenarios if needed
//                    }
//                }
//        );

        getSupportActionBar();
        createTabs();
        toggleHelp();

        String servUri = "tcp://" + getResources().getString(R.string.defaultServerIp) + ":" + getResources().getString(R.string.defaultServerPort);
        establishConnect();
        try{
            connect = new Connect(servUri, getStringfunc("sessionId"), new MemoryPersistence(), this, MQCL);

        }
        catch (MqttException e) {
            e.printStackTrace();
        }
        handler = new Handler();
//       locationProvider = LocationServices.getFusedLocationProviderClient(this);
//        batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // Initialize views inside onCreate

        // Rest of your code...

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                beforeExit();

            }
        };

        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Add callback to the back press dispatcher
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    public void setStringPreference(String key, String value) {
        getDefaultSharedPreferences(this).edit().putString(key, value).apply();
    }
    public void setIntPreference(String key, Integer value) {
        getDefaultSharedPreferences(this).edit().putInt(key, value).apply();
    }
    public void setBoolPreference(String key, Boolean value) {
        getDefaultSharedPreferences(this).edit().putBoolean(key, value).apply();
    }

    private void refreshApp() {
        jsonify.putJson(this, jsonFile, listOfSensors);
        finish();
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
    private void unsubscribe(String topic) {
        if (connect.isConnected()) {
            toggleOn();
            try {
                connect.unsubscribe(topic);
                System.out.println("Unsubscribed successfully from " + topic);
            } catch (MqttException e) {
                System.err.println("Unsubscribed failed from " + topic);
                e.printStackTrace();
            }
        } else {
            toggleOff();
            Toast.makeText(this, "No connection established", Toast.LENGTH_SHORT).show();
        }
    }
    private void subscribe(String topic, int qos) {
        if (connect.isConnected()) {
            toggleOn();
            try {
                connect.setSubTopic(topic);
                connect.subscribe(connect.getSubTopic(), qos);
                System.out.println("Successfully subscribed to " + connect.getSubTopic());
                Toast.makeText(this, "Subscribed to " + topic, Toast.LENGTH_SHORT).show();
            } catch (MqttException e) {
                System.err.println("Subscription  to " + connect.getSubTopic() + "failed") ;
                e.printStackTrace();
            }
        } else {
            toggleOff();
            Toast.makeText(this, "You need to connect first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void publish(String topic, String payload, int qos, boolean retain) {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        message.setRetained(retain);
        if (connect.isConnected()) {
            toggleOn();
            try {
                connect.publish(connect.getPubTopic(), message).waitForCompletion();
                System.out.println("Successfully published \"" + payload + "\" to " + connect.getPubTopic());
                Toast.makeText(this, "Published \"" + payload + "\" to " + topic, Toast.LENGTH_SHORT).show();
            } catch (MqttException e) {
                System.err.println("Publish failure \"" + payload + "\" to " + connect.getPubTopic());
                e.printStackTrace();
            }
        } else {
            toggleOff();
            Toast.makeText(this, "No connection established", Toast.LENGTH_SHORT).show();
        }
    }




    private String getStringfunc(String key) {
        // Get the default SharedPreferences instance
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);

        return sharedPreferences.getString(key, "DEFAULT");
    }

    private Integer getInteger(String key) {
        // Get the default SharedPreferences instance
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);

        return sharedPreferences.getInt(key, -1);
    }
    private Boolean getBool(String key) {
        // Get the default SharedPreferences instance
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);

        return sharedPreferences.getBoolean(key, false);
    }
//    https://developer.android.com/develop/ui/views/components/menus

    private void establishConnect() {
        MQCL = new MQTTCallbackHandler.MQTTCallbackListener(){
            public void onMessageArrived(String topic, MqttMessage message) {
                handler.post(() -> funcMessageArrived(topic, message));
            }

            @Override
            public void onMessageReceived(String topic, MqttMessage message) {
                handler.post(() -> funcMessageReceived(topic, message));
            }

            @Override
            public void onConnectionLost() {
                handler.post(() -> funcConnectionLost());
            }

            @Override
            public void onReconnected() {
                handler.post(() -> funcReconnected());
            }
        };


    }


    private void toggleHelp(){
        toggleOff();
        toggle.setOnClickListener(v->swapConnection());

    }
    private void funcMessageArrived(String topic, MqttMessage message) {
        System.out.println("Arrived " + message + " in " + topic);
        Toast.makeText(connect.getContext(), "Arrived " + message + " in " + topic, Toast.LENGTH_SHORT).show();
    }

    private void funcMessageReceived(String topic, MqttMessage message) {
        System.out.println("Received " + message + " in " + topic);
        Toast.makeText(connect.getContext(), "Received " + message + " in " + topic, Toast.LENGTH_SHORT).show();
    }




    private void HasConnected(){

    }
    private void funcConnectionLost() {
        toggleOff();
        System.out.println("Got disconnected");
        Toast.makeText(this, "Got disconnected ", Toast.LENGTH_SHORT).show();
    }

    private void toggleOff() {
        // initiate a toggle button
        toggle.setChecked(false);
        
    }

    private void funcReconnected() {
        toggleOn();
        System.out.println("Just reconnected");
        Toast.makeText(this, "Just reconnected", Toast.LENGTH_SHORT).show();
    }

    private void toggleOn() {

        toggle.setChecked(true);
    }


    private void failedDisconnect(){
        //forceExit.set(true);
        toggleOn();
        System.err.println("Failed disconnection from"+ connect.getServerUri());
        Toast.makeText(this,"Failed disconnection from "+ connect.getServerUri(),Toast.LENGTH_SHORT).show();
    }

    private void hasDisconnected(){
        forceExit.set(true);
        toggleOff();
        System.err.println("Disconnection from"+ connect.getServerUri());
        Toast.makeText(this,"Disconnected successfully from "+ connect.getServerUri(),Toast.LENGTH_SHORT).show();
    }

    private void failedConnect(){
        //toggle button
        toggleOff();
        System.err.println("Failed connection to"+ connect.getServerUri());
        if(!connect.isInternetServiceAvailable()){
            Toast.makeText(this,"Connection failure no internet connection",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Connection failure port/ip settings are wrong",Toast.LENGTH_SHORT).show();
        }
    }

    private void beforeExit() {
        AlertDialog.Builder mybuilder;
        mybuilder = new AlertDialog.Builder(this);
        mybuilder.setTitle("Are you certain you want to leave the app");
        mybuilder.setMessage("");
        mybuilder.setPositiveButton("Yes", (dialogInterface, i) -> finish());
        mybuilder.setNegativeButton("No I want to stay", (dialogInterface, i) -> dialogInterface.dismiss());
        mybuilder.show();
    }



    private void getDisconnected(){
        try{
            unsubscribe("civil/server/" + connect.getSessionID() + "/#");
            IMqttToken mytoken = connect.disconnect();
            mytoken.waitForCompletion(1500);
            hasDisconnected();
        }
        catch (MqttException e) {
            failedDisconnect();
            e.printStackTrace();
        }
    }

    private void getConnected(){
        try{
            IMqttToken mytoken = connect.connect(connect.getConnectOptions());
            mytoken.waitForCompletion(1500);
            HasConnected();
            subscribe("#", 2);
        }
        catch (MqttException e) {
            failedConnect();
            e.printStackTrace();
        }
    }


    private void swapConnection(){
        if(connect.isConnected()){
            getDisconnected();
        }
        else{
            getConnected();
        }
    }





    //@SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.preferencesMenu) {
                startActivity(new Intent(this, MySettings.class));
                return true;
            }
            else if (item.getItemId() == R.id.createSensorMenu) {
//                Intent sensorIntent = new Intent(this, SensorActivity.class);
////                sensorActivityResultLauncher.launch(sensorIntent);
//                getResult.launch(sensorIntent);
                Intent sensorIntent = new Intent(this, SensorActivity.class);
                startForResult.launch(sensorIntent);
//                startActivity(sensorIntent);
//                System.out.println("I have entered yayyyy\n");
//                SensorActivityResultLauncher.launch(sensorIntent);
                //startActivityForResult(new Intent(this, SensorActivity.class), SENSORCODE);
                return true;
            }
            else if (item.getItemId() == R.id.resetSettingsMenu) {
                String sessionId = getStringfunc("sessionId");
//                boolean isDarkThemeOn = getBool("dark_theme");
                getDefaultSharedPreferences(this).edit().clear().apply();
                // Restore the session ID
                setStringPreference("sessionId", sessionId);
                // Restore the selected theme
                //setBoolPreference("dark_theme", isDarkThemeOn);
                // Delete all extralistOfSensors
                if (listOfSensors.size() > 2)
                    listOfSensors.subList(2, listOfSensors.size()).clear();
                refreshApp();
                return true;
            }
            else if (item.getItemId() == R.id.exitMenu) {
                jsonify.putJson(this, jsonFile, listOfSensors);
                beforeExit();
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
                forceExit.set(true);
                autoCoordinatesPerm.set(false);
                Toast.makeText(this, "You need to give location permission to enable Auto Coordinates mode", Toast.LENGTH_LONG).show();
            }
        }
    }

//    ActivityResultLauncher<Intent> SensorActivityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    System.out.println("MOMO\n");
//                    Intent data = result.getData();
//                    Sensor s = new Sensor(data.getStringExtra("SensorType"), Float.parseFloat(data.getStringExtra("Minimum")),
//                            Float.parseFloat(data.getStringExtra("Maximum")), Float.parseFloat(data.getStringExtra("SliderValue")));
//                    listOfSensors.add(s);
//                    refreshApp();
//                    //doSomeOperations();
//                }
//                else{
//                    System.out.println("result.getResultCode() is " + result.getResultCode());
//                }
//            });

//    @Override
//    private void getResult()

//    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            new ActivityResultCallback<ActivityResult>() {
//                @Override
//                public void onActivityResult(ActivityResult o) {
//
//                }
//
//                @Override
//                public void onActivityResult(int requestCode, int resultCode) {
//                    if (requestCode == SENSORCODE && resultCode == RESULT_OK) {
//                        Sensor s = new Sensor(data.getStringExtra("SensorType"), Float.parseFloat(data.getStringExtra("Minimum")),
//                                Float.parseFloat(data.getStringExtra("Maximum")), Float.parseFloat(data.getStringExtra("SliderValue")));
//                        listOfSensors.add(s);
//                        refreshApp();
//                    }
//                    // Add same code that you want to add in onActivityResult method
//                }
//            });

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == SENSORCODE && resultCode == RESULT_OK) {
//            Sensor s = new Sensor(data.getStringExtra("SensorType"), Float.parseFloat(data.getStringExtra("Minimum")),
//                    Float.parseFloat(data.getStringExtra("Maximum")), Float.parseFloat(data.getStringExtra("SliderValue")));
//            listOfSensors.add(s);
//            refreshApp();
//        }
//    }

    private void createTabs() {

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        MyAdapter myAdapter = new MyAdapter(fragmentManager);
        MyAdapter myadapter = new MyAdapter(MainActivity.this);
        if (getStringfunc("sessionId").equals("DEFAULT")) {
            System.out.println("Entered correctly\n");
            // If this is the first run of the app, load the defaultlistOfSensors
            setStringPreference("sessionId", String.valueOf(new Random().nextInt(10000)));
           listOfSensors = jsonify.getJson(this, jsonFile, true);
            System.out.println("before put json\n");
            jsonify.putJson(this, jsonFile,listOfSensors);
            System.out.println("after put json\n");
        } else {
            // Otherwise load thelistOfSensors the app has stored
           listOfSensors = jsonify.getJson(this, jsonFile, false);
        }
        ViewPager2 ViewPager2 = findViewById(R.id.viewPager);
        ViewPager2.setOffscreenPageLimit(listOfSensors.size() - 1);
        TabLayout tabs = findViewById(R.id.tabs);
        int smokeCounter = 0, gasCounter = 0, tempCounter = 0, uvCounter = 0;
        for (int sensor = 0; sensor <listOfSensors.size(); sensor++) {
            Fragment fragment = null;
            Bundle bundle = new Bundle();
            if(listOfSensors==null){
                System.out.println("NULL???");
            }
            System.out.println(sensor);
            listOfSensors.get(sensor);
//            bundle.putFloatArray("args", new float[]{listOfSensors.get(sensor).getMinimum(),listOfSensors.get(sensor).getMaximum(),listOfSensors.get(sensor).getSliderValue()});
            // Storing individual float values in the Bundle
            bundle.putFloat("ARG_SLIDER_VALUE", listOfSensors.get(sensor).getSliderValue());
            bundle.putFloat("ARG_MINIMUM", listOfSensors.get(sensor).getMinimum());
            bundle.putFloat("ARG_MAXIMUM", listOfSensors.get(sensor).getMaximum());

            switch (listOfSensors.get(sensor).getSensorType()) {
                case "Smoke":
                    fragment = new SmokeFrag();
                    if(fragment==null){
                        System.out.println("Nayeon");
                    }
                    else{

                    }
                    myadapter.addFragment(fragment, getResources().getString(R.string.tabSmoke) + " " + ++smokeCounter);
                    break;
                case "Gas":
                    fragment = new GasFrag();
                    myadapter.addFragment(fragment, getResources().getString(R.string.tabGas) + " " + ++gasCounter);
                    break;
                case "Temp":
                    fragment = new TempFrag();
                    myadapter.addFragment(fragment, getResources().getString(R.string.tabTemp) + " " + ++tempCounter);
                    break;
                case "UV":
                    fragment = new UVFrag();
                    myadapter.addFragment(fragment, getResources().getString(R.string.tabUV) + " " + ++uvCounter);
                    break;
                default:
                    break;
            }
            if (fragment != null) fragment.setArguments(bundle);
        }
        ViewPager2.setAdapter(myadapter);
//        tabs.setupWithViewPager2(ViewPager2);
        new TabLayoutMediator(tabs, ViewPager2,
                (tab, position) -> tab.setText(myadapter.getTitleList((position)))
        ).attach();
    }



    // Returns a map of alllistOfSensors on the current fragments along with their status
    HashMap<Sensor, Boolean> getCurrentSensors() {
        HashMap<Sensor, Boolean> currentSensors = new HashMap<>();
        for (int sensor = 0; sensor <listOfSensors.size(); sensor++) {
            SwitchMaterial switchmat = null;
            Slider slider = null;
            boolean isActive = false;
            // Double-check if the fragment of this sensor is attached
            if (myadapter.createFragment(sensor).isAdded()) {
                switch (listOfSensors.get(sensor).getSensorType()) {
                    case "Smoke":
                        switchmat = myadapter.createFragment(sensor).requireView().findViewById(R.id.SMOKEswitcharoo);
                        slider = myadapter.createFragment(sensor).requireView().findViewById(R.id.SMOKESensorSlider);
                        break;
                    case "Gas":
                        switchmat = myadapter.createFragment(sensor).requireView().findViewById(R.id.GASswitcharoo);
                        slider = myadapter.createFragment(sensor).requireView().findViewById(R.id.UVSensorSlider);
                        break;
                    case "Temp":
                        switchmat = myadapter.createFragment(sensor).requireView().findViewById(R.id.TEMPswitcharoo);
                        slider = myadapter.createFragment(sensor).requireView().findViewById(R.id.TEMPSensorSlider);
                        break;
                    case "UV":
                        switchmat = myadapter.createFragment(sensor).requireView().findViewById(R.id.UVswitcharoo);
                        slider = myadapter.createFragment(sensor).requireView().findViewById(R.id.UVSensorSlider);
                        break;
                    default:
                        break;
                }
            }
            if (switchmat != null){
                isActive = switchmat.isActivated();}
            if (slider != null){
                listOfSensors.get(sensor).setSlider(slider.getValue());
            }
            if (switchmat != null && slider != null) {
                currentSensors.put(listOfSensors.get(sensor), isActive);
            }
        }
        return currentSensors;
    }

    private void onConnectSuccess() {
        toggleOn();
        System.out.println("Successfully connected to " + connect.getServerUri());
        Toast.makeText(this, "Connected to " + connect.getServerIp(), Toast.LENGTH_SHORT).show();
        // Check which location mode is selected
        if (getBool("autoCoords")) {
            // Auto mode connection - Pick coordinates from GPS sensor
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PROVIDER_CODE);
            } else {
                setUpLocationProvider();
            }
        } else {
            // Manual mode connection - Pick coordinates from preferences
            switch (Integer.parseInt(getStringfunc("DeviceLocation"))) {
                case 0:
                    latitude = SUDOLOC[0][0];
                    longitude = SUDOLOC[0][1];
                    break;
                case 1:
                    latitude = SUDOLOC[1][0];
                    longitude = SUDOLOC[1][1];
                    break;
                case 2:
                    latitude = SUDOLOC[2][0];
                    longitude = SUDOLOC[2][1];
                    break;
                case 3:
                    latitude = SUDOLOC[3][0];
                    longitude = SUDOLOC[3][1];
                    break;
            }
            SensorRunnable runnable = new SensorRunnable();
            new Thread(runnable).start();
        }
    }






    @SuppressLint("MissingPermission")
    private void setUpLocationProvider() {
        locationProvider.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                gpsReady.set(true);
                autoCoordinatesPerm.set(true);
                SensorRunnable runnable = new SensorRunnable();
                new Thread(runnable).start();
            }
        });
    }



    private String getBatteryLevel() {
        return String.valueOf(batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
    }



//    @Override
//    public void onBackPressed() {
//        beforeExit();
//    }
// TODO FIX AUTOCOORDS
    class SensorRunnable implements Runnable {
        @Override
        public void run() {
            forceExit.set(false);
            ScheduledExecutorService service = Executors.newScheduledThreadPool(0);
            ScheduledFuture<?> serviceHandler = service.scheduleAtFixedRate(() -> {
                if (forceExit.get()) {
                    service.shutdownNow();
                } else {
                    if (!getBool("autoCoords") || (autoCoordinatesPerm.get() && gpsReady.get())) {
                        HashMap<Sensor, Boolean>listOfSensors = getCurrentSensors();
                        StringBuilder payload = new StringBuilder();
                        payload.append(latitude).append(";").append(longitude).append(";").append(getBatteryLevel()).append(";");
                        // Only append data of currently activelistOfSensors
                        for (Map.Entry<Sensor, Boolean> entry :listOfSensors.entrySet())
                            if (entry.getValue().toString().equals("true"))
                                payload.append(entry.getKey().getSensorType()).append(";").append(entry.getKey().getSliderValue()).append(";");
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


//ta navigate!!