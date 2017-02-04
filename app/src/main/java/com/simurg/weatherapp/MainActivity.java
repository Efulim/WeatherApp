package com.simurg.weatherapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGetWeather = (Button) findViewById(R.id.btnGetWeather);
        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                    ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {

                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        String restAPILink = "https://api.darksky.net/forecast/" + Constants.SECRET_KEY + "/" + String.valueOf(lat) + "," +
                                String.valueOf(lon) + "?lang=tr&units=si&exclude=[minutely,hourly,daily,alerts,flags]";
                        new DarkSkyAPI().execute(restAPILink);
                    }
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.GPS_LOCATION_ERROR), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class DarkSkyAPI extends AsyncTask<String, Void, String> {
        ProgressDialog pdia;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(MainActivity.this);
            pdia.setTitle(getResources().getString(R.string.PLEASE_WAIT));
            pdia.setCanceledOnTouchOutside(false);
            pdia.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (!strings[0].isEmpty()) {
                try {
                    URL url = new URL(strings[0]);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(5000);
                    urlConnection.setConnectTimeout(7500);
                    urlConnection.setDoOutput(false);
                    urlConnection.connect();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    return stringBuilder.toString();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            pdia.dismiss();
            if (!string.isEmpty()) {
                Gson gson = new Gson();
                WeatherJSONObject weatherJSONObject = gson.fromJson(string, WeatherJSONObject.class);

                TextView tvSummary, tvTemperature, tvApparentTemperature,
                        tvHumidity, tvWindSpeed, tvPrecipType;

                tvSummary = (TextView) findViewById(R.id.tvSummary);
                tvSummary.setText(makeString(weatherJSONObject.currently.summary));

                tvTemperature = (TextView) findViewById(R.id.tvTemperature);
                tvTemperature.setText(makeString(String.valueOf(weatherJSONObject.currently.temperature)));

                tvApparentTemperature = (TextView) findViewById(R.id.tvApparentTemperature);
                tvApparentTemperature.setText(makeString(String.valueOf(weatherJSONObject.currently.apparentTemperature)));

                tvHumidity = (TextView) findViewById(R.id.tvHumidity);
                tvHumidity.setText(makeString(String.valueOf(weatherJSONObject.currently.humidity * 100), "%"));

                tvWindSpeed = (TextView) findViewById(R.id.tvWindSpeed);
                tvWindSpeed.setText(makeString(String.valueOf(weatherJSONObject.currently.windSpeed)));

                tvPrecipType = (TextView) findViewById(R.id.tvPrecipType);
                tvPrecipType.setText(makeString(String.valueOf(weatherJSONObject.currently.precipType)));
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.REST_API_REQUST_ERROR), Toast.LENGTH_SHORT).show();
            }
        }

        String makeString(String s1) {
            return s1;
        }
        String makeString(String s1, String s2) {
            return s1 + " " + s2;
        }
    }
}
