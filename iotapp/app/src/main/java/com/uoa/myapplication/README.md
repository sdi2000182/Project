The app facilitates monitoring of various sensors, including smoke, gas, UV, and temperature sensors. 
Users can customize settings such as server connection details, sensor activation, and data transmission preferences.

The application offers a range of features. It supports various sensors like smoke, gas, UV, and temperature sensors, with 
dynamic sensor management allowing users to easily add the remaining sensors (UV and Temp) via the app's menu. 
Data regarding the sensors is transmitted to a server in JSON format for efficient server connectivity. Users have the flexibility to customize settings 
such as IP address, port, session ID, server IP address, QoS level, and location transmission method, with additional settings available for simulation purposes. 
Sensor setup involves default installation of smoke and gas sensors, with options to add UV and temperature sensors through the menu.Upon selecting the "Add Sensor" option there 
are two tabs: UV and Temperature Sensor with a range slider where the user can defined the range of the slider for the specific sensor and a button in order to create the sensor and add the
tab to the main interface.
Sensors can be activated individually using the toggle button below the sensor slider.Using the sensor slider the value of each sensor is defined. 
The ranges for each sensor differ: 0-0.25 for the
smoke sensor, 0-11 for the gas and uv sensor and -5-80 for the temperature sensor.
Establishing a server connection is straightforward with a dedicated "Connect to Server" button. Each IoT device publishes all data destined for the Edge Server ,using the MQTT protocol, 
including measurements from all active sensors, the device's location, and its battery percentage. Note: Each IoT device has its own unique topic for data transmission, ensuring isolation from other devices
this topic in order to be unique uses the device id number with is a unique identifier for the emulator .
Furthermore, users can fine-tune settings through the settings menu to adjust parameters like IP address, port, session ID, server IP address, QoS level, 
and location transmission method.
Data transmission to the server occurs in JSON format and includes parameters such as latitude, longitude, 
battery percentage, and sensor values for active sensors.
To exit the application, the user can use the "Exit" option. Upon selecting this option, a message will be displayed to confirm whether the user wants to exit the 
application.


Note: According to the instructions provided the app is not allowed to make two sensors of the same type! So the user only can add the UV and Temp sensors.