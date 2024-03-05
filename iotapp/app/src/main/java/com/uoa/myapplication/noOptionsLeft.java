package com.uoa.myapplication;

import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.slider.RangeSlider;

import org.jetbrains.annotations.NotNull;

public class noOptionsLeft extends Fragment {


    public interface OptionsListener {
//        void onCreateOptionsLeft();
    }

    private OptionsListener listener;
    private myViewModel viewModel;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            try {
                this.listener = (OptionsListener) activity;
            } catch (final ClassCastException e) {
                throw new ClassCastException(activity.toString() + "must implement options");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_options_left, parent, false);
        viewModel = new ViewModelProvider(requireActivity()).get(myViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize views and set up any necessary UI components
    }

//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.SmokeSensorButton) {
//            updateValues();
//            listener.onCreateSmokeSensor();
//        }
//    }

//    private void updateValues() {
//        RangeSlider slider = requireView().findViewById(R.id.SmokeSensorRange);
//        viewModel.setSmokeMinimum(slider.getValues().get(0));
//        viewModel.setSmokeMaximum(slider.getValues().get(1));
//    }

}