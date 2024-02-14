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