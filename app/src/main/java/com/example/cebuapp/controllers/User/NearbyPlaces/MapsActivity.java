package com.example.cebuapp.controllers.User.NearbyPlaces;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.cebuapp.R;
import com.example.cebuapp.controllers.HomeActivity;
import com.example.cebuapp.controllers.User.LatestNews.JsonParser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity {
    private Spinner spinType;
    private Button btnFind;
    private ImageButton backBtn;
    private SupportMapFragment supportMapFragment;
    private GoogleMap map;
    private  FusedLocationProviderClient fusedLocationProviderClient;
    private double currentLat = 0, currentLong = 0;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        spinType = findViewById(R.id.spin_type);
        btnFind = findViewById(R.id.btn_find);
        backBtn = findViewById(R.id.back_btn);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        String[] placeTypeList = {"atm", "bank", "hospital", "movie_theater", "restaurant"};
        String[] placeNameList = {"ATM", "Bank", "Hospital", "Movie Theater", "Restaurant"};

        spinType.setAdapter(new ArrayAdapter<>(MapsActivity.this,
                android.R.layout.simple_spinner_dropdown_item, placeNameList));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // check permission
        if (ActivityCompat.checkSelfPermission(MapsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            // request permission
            ActivityCompat.requestPermissions(MapsActivity.this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                    , 44);
        }

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get selected position of spinner
                int i = spinType.getSelectedItemPosition();
                // init url
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + // url
                        "?location=" + currentLat + "," + currentLong + // location latitude and longitude
                        "&radius=1000" + // nearby radius
                        "&types=" + placeTypeList[i] + // place type
                        "&sensor=true" + // sensor
                        "&key=" + getResources().getString(R.string.google_map_key);// sensor
                // Execute place task method to download json data
                new PlaceTask().execute(url);

            }
        });

        // back to home btn
        backBtn.setOnClickListener(v-> {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        });
    }

    private void getCurrentLocation() {
        // initialize task location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // when success
                if (location != null) {
                    // when location is not equal to null, get current lattitude
                    currentLat = location.getLatitude();
                    currentLong = location.getLongitude();
                    // sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            // when map is ready
                            map = googleMap;
                            // zoom current location on map
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(currentLat, currentLong), 10
                            ));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // when permission granted
                getCurrentLocation();
            }
        }
    }

    private class PlaceTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            // exec parser
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {
        // init url
        URL url = new URL(string);
        // init connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // connect connection
        connection.connect();
        // init input stream
        InputStream stream = connection.getInputStream();
        // init buffer reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        // init string builder
        StringBuilder builder = new StringBuilder();
        // init string variable
        String line = "";

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        String data = builder.toString();
        reader.close();
        return data;


    }

    private class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>> {
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonParser jsonParser = new JsonParser();
            // init hash map list
            List<HashMap<String, String>> mapList = null;
            // init json Object
            JSONObject object = null;

            try {
                // init json object
                object = new JSONObject(strings[0]);
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            // clear map
            map.clear();
            for (int i=0; i<hashMaps.size(); i++) {
                HashMap<String,String> hashMapList = hashMaps.get(i);
                double lat = Double.parseDouble(hashMapList.get("lat"));
                double lng = Double.parseDouble(hashMapList.get("lng"));
                String name = hashMapList.get("name");
                LatLng latLng = new LatLng(lat, lng);
                MarkerOptions  options = new MarkerOptions();
                options.position(latLng);
                options.title(name);
                map.addMarker(options);
            }
        }
    }
}