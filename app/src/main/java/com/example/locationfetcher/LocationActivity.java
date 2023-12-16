package com.example.locationfetcher;
//import com.example.locationfetcher.l;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.locationfetcher.databinding.ActivityLocationBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
public class LocationActivity extends AppCompatActivity {

    ActivityLocationBinding binding;
    private static final int LOCATION_PERMISSION_REQUEST = 1001;
   private LocationViewModel locationViewModel;
    private Location lastKnownLocation;
    private Location savedLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        //If the view model has a location we update
        if (locationViewModel.getLastKnownLocation() != null) {
            lastKnownLocation = locationViewModel.getLastKnownLocation();
            updateLocationOnScreen(lastKnownLocation);
        }
        // Request location permissions
        requestLocationPermissions();

        // Initialize fusedLocationClient
        fusedLocationClient = new FusedLocationProviderClient(this);

        // Initialize locationRequest with your desired settings
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000) // Update every minute
                .setFastestInterval(5000);

        // Initialize locationCallback to receive location updates
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d("LocationActivity","onLocationResultCalled");
                if (locationResult != null) {
                    lastKnownLocation = locationResult.getLastLocation();
                    locationViewModel.setLastKnownLocation(lastKnownLocation);
                    Log.d("LocationActivity", "Location received: " + lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude());// Update the global variable
                    updateLocationOnScreen(lastKnownLocation);
                }
            }
        };

        // Views
        Button backButton = findViewById(R.id.location_activity_back_button);
        TextView locationDataTextView = findViewById(R.id.location_data_text_view);
        Button startCaptureButton = findViewById(R.id.start_capture_button);
        Button stopCaptureButton = findViewById(R.id.stop_capture_button);
        Button saveCaptureButton = findViewById(R.id.save_capture_button);
        // Actions
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationUpdates();
                // Use an Intent to navigate back to HomeActivity (or HomeFragment)
                Intent intent = new Intent(LocationActivity.this, MainActivity.class);
                if(savedLocation != null) {
                    intent.putExtra("latitude", savedLocation.getLatitude());
                    intent.putExtra("longitude", savedLocation.getLongitude());
                    savedLocation = locationViewModel.getLastKnownLocation();
                }
                startActivity(intent);
                finish(); // Optional: Finish the LocationActivity to remove it from the back stack
            }
        });

        // Start capturing location when the "Start Capture" button is pressed
        startCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();
            }
        });

        // Stop capturing location when the "Stop Capture" button is pressed
        stopCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationUpdates();
            }
        });

        // Save the current latitude and longitude when the "Save" button is pressed
        saveCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastKnownLocation != null){
                    savedLocation = lastKnownLocation;
                }
                // Implement saving logic here
                Toast.makeText(LocationActivity.this, "Location saved!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        }
    }
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        Toast.makeText(this, "Location updates started", Toast.LENGTH_SHORT).show();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Toast.makeText(this, "Location updates stopped", Toast.LENGTH_SHORT).show();
    }

    private void updateLocationOnScreen(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            binding.locationDataTextView.setText(String.format("Latitude: %s\nLongitude: %s", latitude, longitude));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start capturing location
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

