package com.uoa.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.RangeSlider;

import org.jetbrains.annotations.NotNull;

public class TempCreate extends Fragment implements View.OnClickListener {

    public interface TempSensorCreateListener {
        void onCreateTempSensor();
    }

    private TempSensorCreateListener listener;
    private myViewModel viewModel;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            try {
                this.listener = (TempSensorCreateListener) activity;
            } catch (final ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement onCreateTempSensor");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.temp_create, parent, false);
        AppCompatButton createBtn = view.findViewById(R.id.TempButton);
        createBtn.setOnClickListener(this);
        // Setup viewModel to share data with parent activity
        viewModel = new ViewModelProvider(requireActivity()).get(myViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.TempButton) {
            updateValues();
            listener.onCreateTempSensor();
        }
    }

    private void updateValues() {
        RangeSlider slider = requireView().findViewById(R.id.TempRange);
        viewModel.setTempMinimum(slider.getValues().get(0));
        viewModel.setTempMaximum(slider.getValues().get(1));
    }

}