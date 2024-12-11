package com.example.mrrover;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class setLocation extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    EditText fromLocation;
    EditText toLocation;
    Button done;
    Button search;
    private static final String OPENROUTESERVICE_API_KEY = "5b3ce3597851110001cf62489324b9df69104cfcadfb7290175abbdd";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_location);

        fromLocation = findViewById(R.id.from111); // Fixed the missing semicolon
        toLocation = findViewById(R.id.to222);
        done = findViewById(R.id.confirm_booking);
        search = findViewById(R.id.search123);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        search.setOnClickListener(v -> {
            String from = fromLocation.getText().toString();
            String to = toLocation.getText().toString();
            if (!from.isEmpty() && !to.isEmpty()) {
                showRoute(from, to);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*String fromLocationText = fromLocation.getText().toString();
                String ToLocationText = toLocation.getText().toString();

                Intent intent = new Intent(setLocation.this , Booking.class);
                intent.putExtra("From", fromLocationText);
                intent.putExtra("To", ToLocationText);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);*/

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Define the default location (10.3097° N, 123.9484° E)
        LatLng defaultLocation = new LatLng(10.3097, 123.9484);

        // Move the camera to the default location and set a default zoom level
        float zoomLevel = 15.0f; // Adjust the zoom level as needed
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, zoomLevel));
    }

    private void showRoute(String from, String to) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> fromAddresses = geocoder.getFromLocationName(from, 1);
            List<Address> toAddresses = geocoder.getFromLocationName(to, 1);

            if (!fromAddresses.isEmpty() && !toAddresses.isEmpty()) {
                LatLng fromLatLng = new LatLng(fromAddresses.get(0).getLatitude(), fromAddresses.get(0).getLongitude());
                LatLng toLatLng = new LatLng(toAddresses.get(0).getLatitude(), toAddresses.get(0).getLongitude());

                // Add markers for start and end points
                mMap.addMarker(new MarkerOptions().position(fromLatLng).title("Start: " + from));
                mMap.addMarker(new MarkerOptions().position(toLatLng).title("End: " + to));

                // Move the camera to the start location
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fromLatLng, 10));

                // Fetch and draw the route
                fetchRoute(fromLatLng, toLatLng);
            } else {
                Toast.makeText(this, "Invalid locations", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fetchRoute(LatLng from, LatLng to) {
        new Thread(() -> {
            try {
                String url = String.format("https://api.openrouteservice.org/v2/directions/driving-car?api_key=%s&start=%f,%f&end=%f,%f",
                        OPENROUTESERVICE_API_KEY, from.longitude, from.latitude, to.longitude, to.latitude);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray coordinates = jsonResponse.getJSONArray("features")
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONArray("coordinates");

                    List<LatLng> routePoints = new ArrayList<>();
                    for (int i = 0; i < coordinates.length(); i++) {
                        JSONArray point = coordinates.getJSONArray(i);
                        routePoints.add(new LatLng(point.getDouble(1), point.getDouble(0)));
                    }

                    runOnUiThread(() -> drawRouteOnMap(routePoints));
                } else {
                    runOnUiThread(() -> Toast.makeText(setLocation.this, "Failed to fetch route", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(setLocation.this, "API call failed", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void drawRouteOnMap(List<LatLng> routePoints) {
        if (!routePoints.isEmpty()) {
            mMap.addPolyline(new PolylineOptions()
                    .addAll(routePoints)
                    .width(10)
                    .color(getResources().getColor(R.color.blue)));
        }
    }
}