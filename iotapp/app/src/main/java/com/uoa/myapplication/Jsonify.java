package com.uoa.myapplication;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

public class Jsonify {

    public static List<Sensor> retrieveSensorData(Context context, String filename, boolean loadDefault) {
        InputStream inputStream = null;
        StringBuilder stringBuilder = new StringBuilder();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type sensorListType = new TypeToken<List<Sensor>>() {}.getType();

        if (loadDefault) {
            try {
                inputStream = context.getResources().openRawResource(R.raw.smokegas);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) stringBuilder.append(line).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = context.openFileInput(filename);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) stringBuilder.append(line).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return gson.fromJson(stringBuilder.toString(), sensorListType);
    }

    public static void saveSensorData(Context context, String filename, List<Sensor> sensors) {
        FileOutputStream fileOutputStream = null;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        StringBuilder jsonContent = new StringBuilder();
        jsonContent.append("[\n");
        for (int i = 0; i < sensors.size(); i++) {
            jsonContent.append(gson.toJson(sensors.get(i)));
            if (i < sensors.size() - 1) jsonContent.append(",");
            jsonContent.append("\n");
        }
        jsonContent.append("]\n");
        try {
            fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonContent.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}