package com.uoa.sensor2;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.StringJoiner;

public class jsonify {

    public static List<Sensor> getJson(Context context, String file, boolean flag) {
//        InputStream is = null;
        //FileInputStream fis = null;
        StringBuilder sb = new StringBuilder();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type sensorListType = new TypeToken<List<Sensor>>() {}.getType();
        //TODO ISOS LATHOS
        if (flag) {
            try {
                System.out.println("yayy\n");
                InputStream is = context.getResources().openRawResource(R.raw.sensordata);
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                is.close();

                String text = new String(buffer);
                System.out.println("Sensor Data JSON:\n" + text);
                sb.append(text);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                FileInputStream fis = context.openFileInput(file);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();

                String text = new String(buffer);
                sb.append(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("wie gehts\n");
        return gson.fromJson(sb.toString(), sensorListType);

    }

    public static void putJson(Context context, String file, List<Sensor> ListOfSensors) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileOutputStream f_out = null;
        //TODO ISOS LATHOS
//        StringBuilder jsonThing = new StringBuilder();
        StringJoiner joiner = new StringJoiner(",\n", "[\n", "\n]");

        ListOfSensors.stream()
                .map(gson::toJson)
                .forEach(joiner::add);

        String jsonThing = joiner.toString();
        try {
            f_out = context.openFileOutput(file, Context.MODE_PRIVATE);
            f_out.write(jsonThing.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (f_out != null) {
                try {
                    f_out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void putJsonContent(Context context, String file, List<Sensor> sensors) {
        FileOutputStream fos = null;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Convert all the sensor objects to JSON format
        StringBuilder jsonContent = new StringBuilder();
        jsonContent.append("[\n");
        for (int i = 0; i < sensors.size(); i++) {
            jsonContent.append(gson.toJson(sensors.get(i)));
            if (i < sensors.size() - 1) jsonContent.append(",");
            jsonContent.append("\n");
        }
        jsonContent.append("]\n");

        // Store JSON string to given file (create/overwrite)
        try {
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            fos.write(jsonContent.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

