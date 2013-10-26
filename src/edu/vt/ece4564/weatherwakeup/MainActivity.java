/*
 * Erik Wenkel
 * Weather Alarm App
 * 10/26/2013
 * 
 * Main Panel - Displayed upon started and used to set an alarm.
 * Update - Now uses gps/network service senosrs to get the current
 * 			location's zip code.
 */

package edu.vt.ece4564.weatherwakeup;

import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

// Main Activity
public class MainActivity extends Activity implements LocationListener {
	// Global Variables
	TextView currentAlarmText;
	TextView setAlarmText;
	TextView currentLocationText;
	TextView setLocationText;
	Button currentAlarmButton;
	long secondsDelayed = -1;
	String ip;
	String port;
	String loc;
	double lng = 0;
	double lat = 0;

	// Senosr Vaiables
	protected LocationManager locationManager;
	protected Context context;
	protected boolean gps_enabled, network_enabled;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);

			// GUI elements
			currentAlarmText = (TextView) this.findViewById(R.id.textView1);
			setAlarmText = (TextView) this.findViewById(R.id.textView2);
			currentLocationText = (TextView) this.findViewById(R.id.textView3);
			setLocationText = (TextView) this.findViewById(R.id.textView4);
			currentAlarmButton = (Button) this.findViewById(R.id.button1);

			// Listener for button to set alarm
			currentAlarmButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Switch to TimeSet Panel
					startActivity(new Intent(getApplicationContext(),
							TimeSet.class));
				}
			});

			// Setup the location manager
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			// Getting GPS status
			gps_enabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// Getting network status
			network_enabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			// If gps is enabled get any location changes
			if (gps_enabled) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 0, 0, this);
				// If network is enabled get any location changes
			} else if (network_enabled) {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 0, this);
			}

			// Async Thread uses information from TimeSet to set the alarm time
			// and labels
			new SetLabels().execute("");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class SetLabels extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				// Receive data from TimeSet
				Intent i = getIntent();
				ip = i.getStringExtra("IP");
				port = i.getStringExtra("Port");
				String hour = i.getStringExtra("hour");
				String minute = i.getStringExtra("minute");

				// Calculate the time until the alarm should go off
				if (hour != null && minute != null) {
					setAlarmText.setText(hour + ":" + minute);
					Time time = new Time();
					time.setToNow();
					long currentHour = (long) time.hour;
					long currentMinute = (long) time.minute;
					long currentSecond = (long) time.second;
					long currentTime = (((currentHour * 60) + currentMinute) * 60)
							+ currentSecond;
					secondsDelayed = ((Long.valueOf(hour) * 60) + Long
							.valueOf(minute)) * 60;

					if (currentTime > secondsDelayed)
						secondsDelayed = ((24 * 60 * 60 * 1000) - (currentTime * 1000))
								+ (secondsDelayed * 1000);
					else
						secondsDelayed = (secondsDelayed * 1000)
								- (currentTime * 1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String NULL) {
			try {
				if (secondsDelayed != -1) {
					// Timer that waits until the alarms should go off
					new Handler().postDelayed(new Runnable() {
						public void run() {
							try {
								// Switches to the OutputWeather Panel
								Intent intent = new Intent(
										getApplicationContext(),
										OutputWeather.class);

								// Sends the IP, Port, and location to the
								// Output Weather
								// Panel
								intent.putExtra("ip", ip);
								intent.putExtra("port", port);
								intent.putExtra("location", loc);
								startActivity(intent);
							} catch (Exception e) {

							}
						}
					}, secondsDelayed);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		try {
			// If the location has changed get new longitude and latitude
			lat = location.getLatitude();
			lng = location.getLongitude();

			// Google's geocoder api used to get zip code
			Geocoder geocoder = new Geocoder(getApplicationContext(),
					Locale.getDefault());
			List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
			loc = addresses.get(0).getPostalCode();

			// Set location
			setLocationText.setText(loc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}