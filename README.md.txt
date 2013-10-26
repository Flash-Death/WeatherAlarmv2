Weather Alarm App v2
Contributor: Erik Wenkel

Source: https://github.com/hessionb/WeatherAlarmv2

License: GPLv2 or later

License URI: http://www.gnu.org/licenses/gpl-2.0.html

Description

This Andriod Application was designed to display the current weather and temperature at a the current location when an alarm goes off. It was designed so that when you wake up due to an alarm you are automatically given the weather.

The application has three activities corrosponding to the panels that are displayed. Upon startup the MainActivity panel is displayed. From here you can either see the current zip code as gathered by gps/network and the time set for the alarm, if they have been set, or click the Set New Alarm button to switch to the TimeSet panel. In the TimeSet panel you use a time picker to pick the alarm time and enter the IP address and port of the AlarmServer. Once you have picked the setting you want you can click the Set button to go back to the MainActivity panel.

Once you return to the MainActivity the AsyncTask set the location and time labels and also calculates the time until the alarm should go off. Once the time is reached the application switches to the OutputWeather panel and the phone vibrates. The AsyncTask GetWeather triggers a connection to the AlarmServer and retrieves the Json formatted weather for the current location. The Json is then parsed and the weather text is displayed. You can stop the vibation by clicking on the turn off alarm button or causing a large linear/angular acceleration. You can also click the set new alarm button to take you back to the TimeSet panel.

License

Copyright (C) 2013 Erik Wenkel

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.