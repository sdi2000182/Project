package com.uoa.myapplication;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class myViewModel extends ViewModel {

    // Smoke sensor
    private final MutableLiveData<Float> smokeMinimum = new MutableLiveData<>();
    private final MutableLiveData<Float> smokeMaximum = new MutableLiveData<>();
    // Gas sensor
    private final MutableLiveData<Float> gasMinimum = new MutableLiveData<>();
    private final MutableLiveData<Float> gasMaximum = new MutableLiveData<>();
    // Temperature sensor
    private final MutableLiveData<Float> tempMinimum = new MutableLiveData<>();
    private final MutableLiveData<Float> tempMaximum = new MutableLiveData<>();
    // Uv sensor
    private final MutableLiveData<Float> uvMinimum = new MutableLiveData<>();
    private final MutableLiveData<Float> uvMaximum = new MutableLiveData<>();

    public void setSmokeMinimum(Float value) {
        smokeMinimum.setValue(value);
    }

    public MutableLiveData<Float> getSmokeMinimum() {
        return smokeMinimum;
    }
    public MutableLiveData<Float> getTempMinimum() {
        return tempMinimum;
    }

    public void setTempMaximum(Float value) {
        tempMaximum.setValue(value);
    }

    public MutableLiveData<Float> getTempMaximum() {
        return tempMaximum;
    }

    public void setUvMinimum(Float value) {
        uvMinimum.setValue(value);
    }

    public MutableLiveData<Float> getUvMinimum() {
        return uvMinimum;
    }

    public void setUvMaximum(Float value) {
        uvMaximum.setValue(value);
    }

    public MutableLiveData<Float> getUvMaximum() {
        return uvMaximum;
    }

    public void setSmokeMaximum(Float value) {
        smokeMaximum.setValue(value);
    }

    public MutableLiveData<Float> getSmokeMaximum() {
        return smokeMaximum;
    }

    public void setGasMinimum(Float value) {
        gasMinimum.setValue(value);
    }

    public MutableLiveData<Float> getGasMinimum() {
        return gasMinimum;
    }

    public void setGasMaximum(Float value) {
        gasMaximum.setValue(value);
    }

    public MutableLiveData<Float> getGasMaximum() {
        return gasMaximum;
    }

    public void setTempMinimum(Float value) {
        tempMinimum.setValue(value);
    }



}