package com.example.locationfetcher;

import android.location.Location;
import androidx.lifecycle.ViewModel;

public class LocationViewModel extends ViewModel {

    private Location lastKnownLocation;

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(Location location) {
        lastKnownLocation = location;
    }
}
