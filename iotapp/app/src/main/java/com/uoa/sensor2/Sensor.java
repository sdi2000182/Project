package com.uoa.sensor2;
import androidx.annotation.NonNull;

public class Sensor {
    private String SensorType;
    private Float Minimum;
    private Float Maximum;
    private Float SliderValue;
    public Sensor(){}

    public Sensor(String SensorType, Float Minimum, Float Maximum, Float SliderValue) {
        this.SensorType = SensorType;
        this.Minimum = Minimum;
        this.Maximum = Maximum;
        this.SliderValue = SliderValue;
    }
    @NonNull
    @Override
    public String toString() {
        return "SensorType:" + getSensorType() + ";Minimum:" + getMinimum() + ";Maximum:" + getMaximum() + ";SliderValue:" + getSliderValue();
    }
    public Float getMinimum() {
        return Minimum;
    }
    public void setMinimum(Float Minimum) {
        this.Minimum = Minimum;
    }

    public String getSensorType() {
        return SensorType;
    }

    public void setSensorType(String type) {
        this.SensorType = SensorType;
    }

    public Float getMaximum() {
        return Maximum;
    }

    public void setMaximum(Float max) {
        this.Maximum = Maximum;
    }

    public Float getSliderValue() {
        return SliderValue;
    }

    public void setSlider(Float SliderValue) {
        this.SliderValue = SliderValue;
    }

}