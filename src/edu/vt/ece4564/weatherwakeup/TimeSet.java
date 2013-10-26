/*
 * Erik Wenkel
 * Weather Alarm App
 * 10/26/2013
 * 
 * Time Set Panel - Displayed when selecting the time 
 * Update - Takes input for ip and port of Alarm Server
 * 			instead of location.
 */

package edu.vt.ece4564.weatherwakeup;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.Activity;
import android.content.Intent;

public class TimeSet extends Activity {
	// Global Variables
	TextView enterAlarmText;
	TimePicker alarmPicker;
	EditText editIPText;
	EditText editPortText;
	Button setAlarmButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.set_time);

			// GUI elements
			enterAlarmText = (TextView) this.findViewById(R.id.textView5);
			alarmPicker = (TimePicker) this.findViewById(R.id.timePicker1);
			editIPText = (EditText) this.findViewById(R.id.editText1);
			editPortText = (EditText) this.findViewById(R.id.editText2);
			setAlarmButton = (Button) this.findViewById(R.id.button2);

			// Listener to set the alarm
			setAlarmButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Store the ZIP code
					final String ip = editIPText.getText().toString();
					final String port = editPortText.getText().toString();

					// Setup the time string to be sent
					int h = alarmPicker.getCurrentHour();
					final String hour;
					int min = alarmPicker.getCurrentMinute();
					final String minute;

					if (min < 10)
						minute = "0"
								+ alarmPicker.getCurrentMinute().toString();
					else
						minute = alarmPicker.getCurrentMinute().toString();

					if (h < 10)
						hour = "0" + alarmPicker.getCurrentHour().toString();
					else
						hour = alarmPicker.getCurrentHour().toString();

					// Send the ip, port, and time and switch to the Main Panel
					Intent intent = new Intent(getApplicationContext(),
							MainActivity.class);
					intent.putExtra("IP", ip);
					intent.putExtra("Port", port);
					intent.putExtra("hour", hour);
					intent.putExtra("minute", minute);
					startActivity(intent);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}