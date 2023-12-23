package com.uoa.sensor2;

import androidx.lifecycle.ViewModel;

import androidx.lifecycle.MutableLiveData;
public class ViewModel1 extends ViewModel {
    // Smoke sensor
    private final MutableLiveData<Float> TempMinimum = new MutableLiveData<>();
    private final MutableLiveData<Float> GasMinimum = new MutableLiveData<>();

    // Gas sensor
    private final MutableLiveData<Float> UVMinimum = new MutableLiveData<>();

    // Temperature sensor
    private final MutableLiveData<Float> SmokeMinimum = new MutableLiveData<>();
    private final MutableLiveData<Float> SmokeMaximum = new MutableLiveData<>();
    // UV sensor
    private final MutableLiveData<Float> UVMaximum= new MutableLiveData<>();
    private final MutableLiveData<Float> GasMaximum = new MutableLiveData<>();
    private final MutableLiveData<Float> TempMaximum= new MutableLiveData<>();


    public void setSmokeMaximum(Float value) {
        SmokeMaximum.setValue(value);
    }

    public MutableLiveData<Float> getSmokeMaximum() {
        return SmokeMaximum;
    }

    public void setSmokeMinimum(Float value) {
        SmokeMinimum.setValue(value);
    }

    public MutableLiveData<Float> getSmokeMinimum() {
        return SmokeMinimum;
    }

    public void setGasMinimum(Float value) {
        GasMinimum.setValue(value);
    }

    public MutableLiveData<Float> getGasMinimum() {
        return GasMinimum;
    }

    public void setGasMaximum(Float value) {
        GasMaximum.setValue(value);
    }

    public MutableLiveData<Float> getGasMaximum() {
        return GasMaximum;
    }

    public void setTempMinimum(Float value) {
        TempMinimum.setValue(value);
    }

    public MutableLiveData<Float> getTempMinimum() {
        return TempMinimum;
    }

    public void setTempMaximum(Float value) {
        TempMaximum.setValue(value);
    }

    public MutableLiveData<Float> getTempMaximum() {
        return TempMaximum;
    }

    public void setUVMinimum(Float value) {
        UVMinimum.setValue(value);
    }
    public MutableLiveData<Float> getUVMinimum() {
        return UVMinimum;
    }

    public void setUVMaximum(Float value) {
        UVMaximum.setValue(value);
    }

    public MutableLiveData<Float> getUVMaximum() {
        return UVMaximum;
    }
}
