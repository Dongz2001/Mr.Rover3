package com.example.mrrover;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

        fromLocation = findViewById(R.id.from111);
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
                // Handle done button click
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Define the default location (10.3097° N, 123.9484° E)
        LatLng defaultLocation = new LatLng(10.3097, 123.9484);

        // Move the camera to the default location and set a default zoom level
        float zoomLevel = 15.0f;
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

                    // Extract duration from the response
                    JSONArray segments = jsonResponse.getJSONArray("features")
                            .getJSONObject(0)
                            .getJSONObject("properties")
                            .getJSONArray("segments");

                    long durationInSeconds = 0;
                    for (int i = 0; i < segments.length(); i++) {
                        durationInSeconds += segments.getJSONObject(i).getLong("duration");
                    }

                    long durationInMinutes = durationInSeconds / 60;

                    runOnUiThread(() -> {
                        drawRouteOnMap(routePoints, durationInMinutes);
                        Toast.makeText(setLocation.this, "Estimated travel time: " + durationInMinutes + " minutes", Toast.LENGTH_LONG).show();
                    });
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                    runOnUiThread(() -> Toast.makeText(setLocation.this, "Failed to fetch route: " + errorMessage, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(setLocation.this, "API call failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("API call failed", e.getMessage());
                });
            }
        }).start();
    }

    private void drawRouteOnMap(List<LatLng> routePoints, long durationInMinutes) {
        if (!routePoints.isEmpty()) {
            // Draw the first route in blue
            mMap.addPolyline(new PolylineOptions()
                    .addAll(routePoints)
                    .width(10)
                    .color(getResources().getColor(R.color.blue)));

            // Draw the second route in red
            mMap.addPolyline(new PolylineOptions()
                    .addAll(routePoints)
                    .width(10)
                    .color(Color.RED));

            // Calculate the midpoint of the route
            int midpointIndex = routePoints.size() / 2;
            LatLng midpoint = routePoints.get(midpointIndex);

            // Add a custom marker at the midpoint with the estimated travel time
            addCustomMarker(mMap, midpoint, durationInMinutes + " minutes", "");
        }
    }

    public void addCustomMarker(GoogleMap mMap, LatLng position, String time, String distance) {
        // Create a custom view for the marker
        View markerView = LayoutInflater.from(this).inflate(R.layout.custom_marker, null);
        TextView timeTextView = markerView.findViewById(R.id.time_text_view);
        TextView distanceTextView = markerView.findViewById(R.id.distance_text_view);
        timeTextView.setText(time);
        distanceTextView.setText(distance);

        // Create a BitmapDescriptor from the custom view
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(createDrawableFromView(markerView));

        // Add the marker to the map
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(bitmapDescriptor)
                .anchor(0.5f, 1.0f) // Adjust anchor point as needed
        );
    }

    // Helper method to convert a view to a Bitmap
    private Bitmap createDrawableFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }
}