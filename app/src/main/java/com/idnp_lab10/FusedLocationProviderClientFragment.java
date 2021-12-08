package com.idnp_lab10;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class FusedLocationProviderClientFragment extends Fragment {

    private Button btnStart;
    private TextView textViewinformation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for(Location location: locationResult.getLocations()) {

                String auxText = textViewinformation.getText().toString();
                String locationText =  "Longitud: " + location.getLongitude() +
                        " Latitud: " + location.getLatitude() +
                        " Altitud: " + location.getAltitude();
                write(locationText);
                textViewinformation.setText(auxText + "\n" + locationText);
            }
        }
    };

    private static final String TAG = "MainActivity";
    int LOCATION_REQUEST_CODE = 10001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fused_location_provider_client, container, false);

        textViewinformation = view.findViewById(R.id.textViewInformation);

        // Read file
        Context context = getContext();
        String [] files = context.fileList();

        if (fileExists(files, "locationFusedProviderClient.txt")) {
            try {
                InputStreamReader file = new InputStreamReader(context.openFileInput("locationFusedProviderClient.txt"));
                BufferedReader br = new BufferedReader(file);
                String line = br.readLine();
                StringBuilder fullText = new StringBuilder();
                while (line != null) {
                    fullText.append(line).append(System.getProperty("line.separator"));;
                    line = br.readLine();
                }
                br.close();
                file.close();
                textViewinformation.setText(fullText);
            } catch (IOException e) {
            }
        }


        btnStart = view.findViewById(R.id.btnStart);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setSmallestDisplacement(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSettingsAndStartLocationUpdates();
            }
        });
        return view;
    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();

        SettingsClient client = LocationServices.getSettingsClient(getContext());

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(getActivity(), 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSettingsAndStartLocationUpdates();
            }
        }
    }

    private boolean fileExists(String[] files, String file) {
        for (int f = 0; f < files.length; f++)
            if (file.equals(files[f]))
                return true;
        return false;
    }

    public void write(String text) {
        Context context = getContext();
        try {
            OutputStreamWriter file = new OutputStreamWriter(context.openFileOutput("locationFusedProviderClient.txt", context.MODE_APPEND));
            file.append(text + "\n");
            file.close();
        } catch (IOException e) {
        }
    }
}