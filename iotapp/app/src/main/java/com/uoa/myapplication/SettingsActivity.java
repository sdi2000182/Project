package com.uoa.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    int sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        this.sessionId = Integer.parseInt(readStringSetting("sessionId"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().popBackStackImmediate()) return true;
        return super.onSupportNavigateUp();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // Update connection values
        switch (key) {
            case "time_out_time":
                // Add desired handling
                break;
            case "sessionId":
                if (isNumeric(readStringSetting(key))) {
                    sessionId = Integer.parseInt(readStringSetting(key));
                } else {
                    setStringSetting(key, String.valueOf(sessionId));
                    refreshUI();
                    Toast.makeText(this, "Please provide a numeric ID", Toast.LENGTH_LONG).show();
                }
                break;
            case "lwRetain":
                // Add desired handling
                break;
            case "autoCoords":
                // Add desired handling
                break;
            case "location":
                // Add desired handling
                break;
            case "ssl":
                // Add desired handling
                break;
            case "lwPayload":
                // Add desired handling
                break;
            case "lwTopic":
                // Add desired handling
                break;
            case "lwQOS":
                // Add desired handling
                break;
            case "password":
                // Add desired handling
                break;
            case "qos":
                // Add desired handling
                break;
            case "serverIp":
                refreshUI();
                break;
            case "use_auth":
                // Add desired handling
                break;
            case "serverPort":
                if (!isNumeric(readStringSetting(key))) {
                    setStringSetting(key, String.valueOf(1883));
                    refreshUI();
                    Toast.makeText(this, "Port number must be an integer", Toast.LENGTH_LONG).show();
                } else {
                    // Add desired handling
                }
                break;
            case "username":
                // Add desired handling
                break;
            default:
                break;
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        public SettingsFragment() {
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.rootsprefs, rootKey);
        }
    }

    public static class LastWillSettings extends PreferenceFragmentCompat {
        public LastWillSettings() {
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.lastwill, rootKey);
        }
    }

    public static class SecuritySettings extends PreferenceFragmentCompat {
        public SecuritySettings() {
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.securityprefs, rootKey);
        }
    }

    private void toggleTheme(String key) {

        if ((this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
            try {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(key, true).apply();
                Toast.makeText(this, "Dark theme enabled", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(key, false).apply();
                Toast.makeText(this, "Dark theme disabled", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // Retrieves value of string-type setting "key"
    private String readStringSetting(String key) {
        return PreferenceManager.getDefaultSharedPreferences(this).getString(key, "-1");
    }

    // Retrieves value of int-type setting "key"
    private Integer readIntSetting(String key) {
        return PreferenceManager.getDefaultSharedPreferences(this).getInt(key, -1);
    }

    // Retrieves value of switch-type setting "key"
    private Boolean readBooleanSetting(String key) {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(key, false);
    }

    // Sets the "key" string setting to the desired "value"
    private void setStringSetting(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(key, value).apply();
    }

    // Sets the "key" int setting to the desired "value"
    private void setIntSetting(String key, Integer value) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(key, value).apply();
    }

    // Sets the "key" switch setting to the desired "value"
    private void setBooleanSetting(String key, Boolean value) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(key, value).apply();
    }

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private void refreshUI() {
        finish();
        startActivity(new Intent(this, com.uoa.myapplication.SettingsActivity.class));
        overridePendingTransition(0, 0);
    }

}