/*
 * Erik Wenkel
 * Weather Alarm App
 * 10/26/2013
 * 
 * Weather Output Panel - Displayed upon the alarm going off.
 * Update - Now contacts only my Alarm Server
 */

package edu.vt.ece4564.weatherwakeup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class OutputWeather extends Activity implements SensorEventListener {
	// Global Variables
	TextView weatherText;
	Button turnOffAlarmButton;
	Button resetAlarmButton;
	String ip = "";
	String port = "";
	String loc = "";
	ImageView image;
	Vibrator vibration;

	// Sersor vaiables
	private SensorManager sensorManager;
	double pasum = 0;
	double pgsum = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.weather_ouput);

			// GUI elements
			weatherText = (TextView) this.findViewById(R.id.textView7);
			turnOffAlarmButton = (Button) this.findViewById(R.id.button3);
			resetAlarmButton = (Button) this.findViewById(R.id.button4);
			image = (ImageView) this.findViewById(R.id.imageView1);

			// Set vibration for when alarm goes off
			vibration = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = { 0, 100, 1000 };
			vibration.vibrate(pattern, 0);

			// Setup sensor manager and register linear acceleration and
			// gyroscope
			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			sensorManager.registerListener(this, sensorManager
					.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
					SensorManager.SENSOR_DELAY_NORMAL);
			sensorManager.registerListener(this,
					sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
					SensorManager.SENSOR_DELAY_NORMAL);

			// Listener to turn off the alarm vibration
			turnOffAlarmButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v2) {
					// Cancel Vibration
					vibration.cancel();
				}
			});

			// Listener to set a new alarm
			resetAlarmButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v1) {
					// Switch to the TimeSet Panel
					startActivity(new Intent(getApplicationContext(),
							TimeSet.class));
				}
			});

			// Receive Location Data
			Intent i = getIntent();
			ip = i.getStringExtra("ip");
			port = i.getStringExtra("port");
			loc = i.getStringExtra("location");

			// Start AsyncTask
			if (ip != "" && port != "" && loc != "")
				new GetWeather().execute("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// AsyncTask for networking
	private class GetWeather extends AsyncTask<String, Void, String> {
		// Data
		String extractedData1 = "";
		String extractedData2 = "";

		@Override
		protected String doInBackground(String... params) {
			try {
				// Networking Variables
				String line;
				String html;
				StringBuilder htmlBuilder = new StringBuilder();

				// Get information from Alarm server
				URL url = new URL("http", ip, Integer.parseInt(port),
						"/?location=" + loc);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("GET");

				BufferedReader readIn = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));

				// Create HTML document
				while ((line = readIn.readLine()) != null) {
					htmlBuilder.append(line);
				}

				html = htmlBuilder.toString();
				readIn.close();

				// Extract relevant data (Json format)
				extractedData1 = html.substring(
						html.indexOf("{\"extractedData1\":\"") + 19,
						html.indexOf("\",\"extractedData2\":\""));
				extractedData2 = html.substring(
						html.indexOf("\"extractedData2\":\"") + 18,
						html.indexOf("\"}"));

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String NULL) {
			try {
				// Set the weather output text
				extractedData1 = extractedData1.substring(0,
						extractedData1.indexOf(" "))
						+ "\n"
						+ extractedData1.substring(extractedData1.indexOf(" "))
						+ "\n";
				extractedData2 = extractedData2.substring(0,
						extractedData2.indexOf(" "))
						+ "\n"
						+ extractedData2.substring(extractedData2.indexOf(" "))
						+ "\n";
				weatherText.setText(extractedData1 + extractedData2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// If rapid linear acceleration, turn off alarm
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];
			double sum = x * x + y * y + z * z;
			sum = Math.sqrt(sum);

			if ((Math.abs(sum - pasum) > 4)) {
				vibration.cancel();
				pasum = 0;
			} else {
				pasum = sum;
			}
		} else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			// If rapid angular acceleration, turn off alarm
			double x = event.values[0];
			double y = event.values[1];
			double z = event.values[2];
			double sum = x * x + y * y + z * z;
			sum = Math.sqrt(sum);

			if ((Math.abs(sum - pgsum) > 4)) {
				vibration.cancel();
				pgsum = 0;
			} else {
				pgsum = sum;
			}
		}
	}
}
