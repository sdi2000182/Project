package com.uoa.sensor2;


import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
public class SecuritySettings extends PreferenceFragmentCompat {

    public SecuritySettings() {
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.securityprefs, rootKey);
    }

}


//settings->mysettingsfragment->securitysettings->lastwill kai edit xml