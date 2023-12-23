package com.uoa.sensor2;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.preference.PreferenceManager;

import java.util.Objects;
import java.util.Random;

import android.content.Intent;

public class MySettings extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    int sessionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings1, new MySettingsFragment())
                    .commit();
        }
//        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            preferences.registerOnSharedPreferenceChangeListener(this);

//        this.sessionId = Integer.parseInt(readStringSetting("session_id"));
        if (getStringfunc("sessionId").equals("DEFAULT")) this.sessionId = new Random().nextInt(10000);
        else this.sessionId = Integer.parseInt(getStringfunc("sessionId"));
        setString("session_id", String.valueOf(sessionId));
    }

    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().popBackStackImmediate()) return true;
        return super.onSupportNavigateUp();
    }

    private boolean isANumber(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?");
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        if (getSupportFragmentManager().popBackStackImmediate()) return true;
//        return super.onSupportNavigateUp();
//    }
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (Objects.requireNonNull(key)) {
            case "sessionId":
                if (isANumber(getStringfunc(key))) {
                    sessionId = Integer.parseInt(getStringfunc(key));
                } else {
                    setString(key, String.valueOf(sessionId));
                    refreshApp();
                    Toast.makeText(this, "Please provide a numeric ID", Toast.LENGTH_LONG).show();
                }
                break;
//            case "time_out_time":
//
//                break;
//            case "retain":
//
//                break;
//            case "ssl":
//
//                break;
//            case "keep_alive_time":
//
//                break;
//            case "payload":
//
//                break;
//            case "topic":
//
//                break;
//            case "qos_levels":
//
//                break;
//            case "password":
//
//                break;
//            case "qos":
//
//                break;
//            case "serverIp":
//
//                break;
//            case "Authentication":
//
//                break;
            case "ServerPort":
                if (!isANumber(getStringfunc(key))) {
                    setString(key, String.valueOf(1883));
                    refreshApp();
                    Toast toast = Toast.makeText(this, "Error port number should be an integer", Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
//            case "Username":
//
//                break;
            default:
                break;
        }

    }
//    Consider using `apply()` instead; `commit` writes its data to persistent storage immediately, whereas `apply` will handle it in the background
    private String getStringfunc(String key) {
        // Get the default SharedPreferences instance
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        return sharedPreferences.getString(key, "DEFAULT");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get the SharedPreferences instance
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Register the SharedPreferences listener
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Get the SharedPreferences instance
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Register the SharedPreferences listener
        preferences.registerOnSharedPreferenceChangeListener(this);
    }


    private void setString(String key, String value) {
        // Get the default SharedPreferences instance
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Create a SharedPreferences.Editor without using edit()
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Remove the existing value associated with the key (if any)
        editor.remove(key);

        // Put the string value associated with the provided key
        editor.putString(key, value);

        // Commit the changes by applying them synchronously
        editor.apply();
    }

    private void refreshApp() {
        // Create an Intent for the SettingsActivity class
        Intent intent = new Intent(this, com.uoa.sensor2.MySettings.class);

        // Set flags to clear the previous activities in the stack and start the new activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Start the new activity
        startActivity(intent);
    }


//    private Integer getInteger(String key) {
//        // Get the default SharedPreferences instance
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//
//        // Retrieve the String value associated with the provided key
//
//        return sharedPreferences.getInt(key, -1);
//    }
//
//

}