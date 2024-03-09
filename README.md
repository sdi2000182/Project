# **Edge Server** 
<br>
There are 7 files in the edge server. 
Main 
<br>
AndroidData <br>
Data <br>
DbConnection <br>
Functions <br>
IotDevice <br>
MessageProcessor <br>
Main: <br>
The server connects to the mosquitto broker and to the topics of the android and the iot 
devices.It receives the messages and passes them to the MessageProcessor. <br>
<ins>MessageProcessor: </ins>
MessageProcessor receives the messages in the 2 topics and handles them accordingly.For the 
Iot devices it creates data objects with all the values and uses insertData for the SQL 
database.For the android  it retrieves its location and also uses insertAndroidData for the SQL 
database.It has 2 more methods for danger check and midpoint calculations. <br>
<ins>AndroidData: </ins>
Class with methods for the data of the android device(get and insertAndroidData). <br>
<ins>Data: </ins>
Class with methods for the handling of the IoT device data)(get,set,timestamp and insertData). <br>
<ins>DbConnection: </ins>
Method for the connection to the sql database and close of the connection. <br>
<ins>Functions: </ins>
Method for the distance calculation. <br>
<ins>IotDevice: </ins>
Class with methods for calculations of distances,dangers etc. <br>
<ins>WebApplication: </ins>
Application using Javascript that displays on the map the IotDevices and the Android device 
through the fetch of the data(longitude,latitude etc) from the API. <br>
<ins>Server: </ins>
API that connects to the Database and retrieves the latest data of the android information and 
the iot information using Express.js . <br><br>

# **Phone App**

This application resides within the mobile device of the user who seeks to receive notifications in 
the event of a detected risk. Upon activation, the user establishes a connection with the MQTT server
via the application, facilitating the reception of audiovisual alerts pertaining to any identified risks.

Upon initiation, the user is directed to the primary interface of the application, presenting various options,
including an Enable/Disable toggle for immediate cessation of measurement transmission to the server.
Additionally, a menu accessible via breadcrumb offers two choices: 
I. Navigate to Settings. 
II. Exit the application. Exiting prompts a confirmation dialogue seeking the user's affirmation to
permanently terminate the application.

Within the Settings menu, users encounter several configurable parameters: Edit IP Address allows 
for the input of the communication IP address with the Edge Server; Port permits specification of the
communication port with the Edge Server. Manual Location provides the option to toggle between GPS-based
or predefined location acquisition. If manual selection is made, two additional settings become available:
Choose File selects between provided files for pronunciation; TimeSpace designates the interval for 
transmitting measurement vectors.

Periodically, at intervals of 10 seconds, the application verifies its internet connectivity through the 
ConnectivityManager. Notification of connectivity loss is promptly relayed to the user interface.


Regarding location acquisition, the application employs two methods: automatic and manual. 
Automatic mode utilizes the FusedLocationProviderClient service to ascertain the user's current location.
Conversely, manual mode involves parsing XML files for pronunciation, extracting relevant vectors, 
and storing them in corresponding tables for subsequent data transmission to the MQTT Server.

Communication with the MQTT Server is facilitated for both receiving notifications and transmitting 
data via distinct topics. To receive distress notifications, the application subscribes to the "notifications"
topic, extracting pertinent data from received JSON files, including hazard level and distance from 
the hazard point. Conversely, data transmission to the server occurs via subscription to the android topic 
encapsulating location vectors and device identification within transmitted JSON files.

# **IoT App**

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