package com.example.cebuapp.controllers.User.NearbyPlaces;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import com.example.cebuapp.Helper.HelperUtilities;
import com.example.cebuapp.R;
import com.example.cebuapp.controllers.HomeActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
    private ImageButton backBtn;
    private SupportMapFragment supportMapFragment;
    private GoogleMap map;
    private  FusedLocationProviderClient fusedLocationProviderClient;
    private double currentLat = 0, currentLong = 0;
    private Handler handler;
    private ProgressDialog dialog;
    private Intent intent;
    private String[] placeTypeList;
    private String[] placeNameList;
    private TextView normalView, satelliteView;
    private SearchView searchView;
    private ImageButton myLocation;
    private List<Address> addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        spinType = findViewById(R.id.spin_type);
        backBtn = findViewById(R.id.back_btn);
        myLocation = findViewById(R.id.myLocation);
        normalView = findViewById(R.id.normal);
        satelliteView = findViewById(R.id.satellite);
        searchView = findViewById(R.id.searchView);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        handler = new Handler();
        dialog = new ProgressDialog(this);
        dialog.setTitle("CEBUAPP");
        addressList = null;

        placeTypeList = new String[]{"none", "restaurant", "tourist_spot", "atm", "bank", "hospital", "movie_theater"};
        placeNameList = new String[]{"Choose an area", "Food Areas", "Tourist Spots", "ATM", "Bank", "Hospital", "Movie Theater"};

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

        setListeners();
    }

    private void setListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String searchedLocation = searchView.getQuery().toString();
                dialog.setMessage("Searching for ''" + searchedLocation + "''...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                map.clear();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();

                        if (searchedLocation != null && !searchedLocation.equals("")) {
                            Geocoder geocoder = new Geocoder(MapsActivity.this);
                            try {
                                addressList = geocoder.getFromLocationName(searchedLocation, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // check if has result
                            if (addressList.size() != 0) {
                                Address address = addressList.get(0);
                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                map.addMarker(new MarkerOptions().position(latLng).title(searchedLocation));
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            } else {

                                HelperUtilities.showOkAlert(MapsActivity.this,
                                        "Sorry, your searched location \n''" + searchedLocation + "'' is not found and not yet added by Google Map.");
                            }

                        }
                    }
                }, 1000);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
//        supportMapFragment.getMapAsync((OnMapReadyCallback) MapsActivity.this);

        normalView.setOnClickListener(v-> {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            normalView.setVisibility(View.GONE);
            satelliteView.setVisibility(View.VISIBLE);
        });

        satelliteView.setOnClickListener(v-> {
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            normalView.setVisibility(View.VISIBLE);
            satelliteView.setVisibility(View.GONE);
        });

        spinType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
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

                    // showing result
                    dialog.setMessage("Searching " + spinType.getSelectedItem().toString() + "...");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            new AlertDialog.Builder(MapsActivity.this)
                                    .setTitle("CebuApp")
                                    .setMessage("REQUEST_DENIED: You must enable Billing on the Google Cloud Project ($200 on first recurring)." +
                                            "\n\nDo you want to check the API response on Chrome?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            // show url result on chrome
                                            Intent intent = new Intent();
                                            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.setPackage("com.android.chrome");
                                            try {
                                                MapsActivity.this.startActivity(intent);
                                            } catch (ActivityNotFoundException ex) {
                                                // Chrome browser presumably not installed so allow user to choose instead
                                                intent.setPackage(null);
                                                MapsActivity.this.startActivity(intent);
                                            }
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            getCurrentLocation();
                                        }
                                    }).create().show();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // back to home btn
        backBtn.setOnClickListener(v-> {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        });

        myLocation.setOnClickListener(v-> {
            getCurrentLocation();
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
                            LatLng latLng = new LatLng(currentLat, currentLong);
                            // when map is ready
                            map = googleMap;
                            // set title
                            MarkerOptions options = new MarkerOptions().position(latLng).title("Your are here.");
                            // zoom current location on map
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            map.addMarker(options);
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



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(new Intent(getApplicationContext(), HomeActivity.class)));
        finish();
    }
}