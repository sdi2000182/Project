package com.uoa.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.slider.Slider;

import org.jetbrains.annotations.NotNull;

public class SmokeFrag extends Fragment {

    private Float Minimum;
    private Float Maximum;
    private Float SliderValue;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        assert getArguments() != null;
        setMinimum(getArguments().getFloatArray("args")[0]);
        setMaximum(getArguments().getFloatArray("args")[1]);
        setSliderValue(getArguments().getFloatArray("args")[2]);
        return inflater.inflate(R.layout.smoke, parent, false);
    }

    public SmokeFrag(){

    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        Slider slider = view.findViewById(R.id.smokeSensorSlider);
        slider.setValueFrom(getMinimum());
        slider.setValueTo(getMaximum());
        slider.setValue(getSliderValue());
    }

    public Float getMinimum() {
        return Minimum;
    }

    public void setMinimum(Float Minimum) {
        this.Minimum = Minimum;
    }

    public Float getMaximum() {
        return Maximum;
    }

    public void setMaximum(Float Maximum) {
        this.Maximum = Maximum;
    }

    public Float getSliderValue() {
        return SliderValue;
    }

    public void setSliderValue(Float SliderValue) {
        this.SliderValue = SliderValue;
    }

}