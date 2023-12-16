package com.example.locationfetcher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.locationfetcher.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private TextView placeholderTextView;
    private String saveImageUrl;
    private ImageView testImage;
    private String imageVisibility;
    private String placeHolderTextVisibility;
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_PLACEHOLDER_TEXT = "placeholder_text";
    private static final String KEY_IMAGE_URL_TEXT = "imageUrl_text";
    private static final String KEY_IMAGE_VISIBILITY = "image_visibility";
    private static final String KEY_PlACEHOLDER_TEXT_VISIBILITY = "placeholder_text_visibility";

    private Boolean placeHolderIsImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        placeHolderTextVisibility = preferences.getString(KEY_PlACEHOLDER_TEXT_VISIBILITY, "true");
        // Declare Views
        placeholderTextView = findViewById(R.id.placeholder_textView);
     //   if (placeHolderTextVisibility == "true") {
            // Restore text from SharedPreferences
            String placeholderText = preferences.getString(KEY_PLACEHOLDER_TEXT, "No location saved");
            placeholderTextView.setText(placeholderText);


     //   }
        Button locationActivityButton = findViewById(R.id.location_activity_button);
        // Define Clicks
        locationActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LocationActivity.class);
                startActivity(i);
            }
        });

        // Check if there are extras in the intent and update placeholderTextView
        Intent intent = getIntent();

        //If we are coming from the LocationActivity and a Location was saved
        if (intent.hasExtra("latitude") && intent.hasExtra("longitude")) {
            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);

            // Check if both latitude and longitude are not null
            if (latitude != 0.0 && longitude != 0.0) {
                // Update placeholderTextView with the received data
                placeholderTextView.setText(String.format("Saved Location : \n Latitude: %s\nLongitude: %s", latitude, longitude));

                // Save text to SharedPreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(KEY_PLACEHOLDER_TEXT, placeholderTextView.getText().toString());
                //show text
                placeholderTextView.setVisibility(View.VISIBLE);
                editor.putString(KEY_PlACEHOLDER_TEXT_VISIBILITY, "true");

                editor.apply();
            }

        }
    }
}