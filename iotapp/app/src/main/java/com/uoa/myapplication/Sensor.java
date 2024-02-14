package com.uoa.myapplication;

import androidx.annotation.NonNull;

public class Sensor {

    private String type;
    private Float Minimum;
    private Float Maximum;
    private Float SliderValue;

    public Sensor() {}

    public Sensor(String type, Float Minimum, Float Maximum, Float SliderValue) {
        this.type = type;
        this.Minimum = Minimum;
        this.Maximum = Maximum;
        this.SliderValue = SliderValue;
    }

    @NonNull
    @Override
    public String toString() {
        return "type:" + getType() + ";Minimum:" + getMinimum() + ";Maximum:" + getMaximum() + ";SliderValue:" + getSliderValue();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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