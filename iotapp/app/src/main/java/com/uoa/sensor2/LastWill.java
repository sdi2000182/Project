package com.uoa.sensor2;


import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

//The MQTT protocol allows a client to provide an optional Last Will Testament (LWT) when connecting to
//        a broker.
//        When provided, the broker will publish a message to the given topic as soon as it lost the connection with
//        the client and didnt recieved a proper disconnect message.
//        The last will is a way to notify other clients that a client has lost it’s connection.
public class LastWill extends PreferenceFragmentCompat {

    public LastWill() {
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.lastwill, rootKey);
    }

}