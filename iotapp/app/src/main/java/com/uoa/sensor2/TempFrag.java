package com.uoa.sensor2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.slider.Slider;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UVFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TempFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final float ARG_SLIDER_VALUE = 5.5f;
    private static final float ARG_MINIMUM = 0.0f;
    private static final float ARG_MAXIMUM = 11.0f;

    // TODO: Rename and change types of parameters
    private float SliderValue;
    private float Minimum;
    private float Maximum;

    public TempFrag() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static TempFrag newInstance(float sliderValue, float minimum, float maximum) {
        TempFrag fragment = new TempFrag();
        Bundle args = new Bundle();
        args.putFloat("ARG_SLIDER_VALUE", sliderValue);
        args.putFloat("ARG_MINIMUM", minimum);
        args.putFloat("ARG_MAXIMUM", maximum);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            SliderValue = getArguments().getFloat("ARG_SLIDER_VALUE");
            Minimum = getArguments().getFloat("ARG_MINIMUM");
            Maximum = getArguments().getFloat("ARG_MAXIMUM");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_u_v, container, false);
    }

    public Float getMinimum() {
        return Minimum;
    }
    public void setMinimum(Float Minimum) {
        this.Minimum = Minimum;
    }


    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        Slider slider = view.findViewById(R.id.TEMPSensorSlider);
        slider.setValueFrom(getMinimum());
        slider.setValueTo(getMaximum());
        slider.setValue(getSliderValue());
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