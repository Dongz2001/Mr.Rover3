package com.example.mrrover;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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

import java.io.IOException;
import java.util.List;

public class setLocation extends FragmentActivity implements OnMapReadyCallback {


    GoogleMap mMap;
    EditText fromLocation;
    EditText toLocation;
    Button btnGetRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);

        fromLocation = findViewById(R.id.from111);
        toLocation = findViewById(R.id.to222);
        btnGetRoute = findViewById(R.id.confirm_booking);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnGetRoute.setOnClickListener(v -> {
            String from = fromLocation.getText().toString();
            String to = toLocation.getText().toString();
            if (!from.isEmpty() && !to.isEmpty()) {
                showRoute(from, to);
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
        // Clear the map to refresh and remove existing markers and polylines
        mMap.clear();

        Geocoder geocoder = new Geocoder(this);
        LatLngBounds cebuBounds = new LatLngBounds(
                new LatLng(9.2144, 123.5897), // Southwest bounds
                new LatLng(11.229, 125.2906)  // Northeast bounds
        );

        try {
            // Geocode from location within Cebu bounds
            List<Address> fromAddresses = geocoder.getFromLocationName(from, 1,
                    cebuBounds.southwest.latitude, cebuBounds.southwest.longitude,
                    cebuBounds.northeast.latitude, cebuBounds.northeast.longitude);
            // Geocode to location within Cebu bounds
            List<Address> toAddresses = geocoder.getFromLocationName(to, 1,
                    cebuBounds.southwest.latitude, cebuBounds.southwest.longitude,
                    cebuBounds.northeast.latitude, cebuBounds.northeast.longitude);

            if (!fromAddresses.isEmpty() && !toAddresses.isEmpty()) {
                LatLng fromLatLng = new LatLng(fromAddresses.get(0).getLatitude(), fromAddresses.get(0).getLongitude());
                LatLng toLatLng = new LatLng(toAddresses.get(0).getLatitude(), toAddresses.get(0).getLongitude());

                mMap.addMarker(new MarkerOptions()
                        .position(fromLatLng)
                        .title("From: " + from)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                mMap.addMarker(new MarkerOptions()
                        .position(toLatLng)
                        .title("To: " + to)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                // Move and zoom the camera to show both markers
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(fromLatLng);
                builder.include(toLatLng);
                LatLngBounds bounds = builder.build();
                int padding = 100; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);

                // Draw a line between the two points
                mMap.addPolyline(new PolylineOptions().add(fromLatLng, toLatLng).width(10).color(Color.RED));

            } else {
                Toast.makeText(this, "Unable to geocode locations within Cebu", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}