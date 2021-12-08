package com.idnp_lab10;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;

public class LocationManagerFragment extends Fragment implements LocationListener {

    private Button btnStart;
    private TextView textViewinformation;
    private LocationManager locationManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_manager, container, false);

        textViewinformation = view.findViewById(R.id.textViewInformation);
        btnStart = view.findViewById(R.id.btnStart);

        // Runtime permissions
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

        // Read file
        Context context = getContext();
        String [] files = context.fileList();

        if (fileExists(files, "location.txt")) {
            try {
                InputStreamReader file = new InputStreamReader(context.openFileInput("location.txt"));
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

        return view;
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this.getContext(), "" + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT);
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = "Longitud: " + location.getLongitude() +
                    " Latitud: " + location.getLatitude();

            Log.i("FragmentLocationManager", "Executando...");
            String auxText = textViewinformation.getText().toString();
            textViewinformation.setText(auxText + "\n" + address);
            write(address);
        } catch (Exception e) {
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

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
            OutputStreamWriter file = new OutputStreamWriter(context.openFileOutput("location.txt", context.MODE_APPEND));
            file.append(text + "\n");
            file.close();
        } catch (IOException e) {
        }
    }
}