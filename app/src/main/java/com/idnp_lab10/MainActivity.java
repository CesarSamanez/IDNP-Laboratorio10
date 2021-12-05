package com.idnp_lab10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    FragmentTransaction transaction;
    Fragment fragmentLocationManager, fragmentLocationProviderClientFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFragments();

        getSupportFragmentManager().beginTransaction().add(R.id.containerFragment, fragmentLocationManager).commit();
    }

    public void onClick(View view) {
        transaction = getSupportFragmentManager().beginTransaction();

        switch (view.getId()) {
            case R.id.btnOption1:
                transaction.replace(R.id.containerFragment, fragmentLocationManager);
                transaction.addToBackStack(null);
                break;

            case R.id.btnOption2:
                transaction.replace(R.id.containerFragment, fragmentLocationProviderClientFragment);
                transaction.addToBackStack(null);
                break;
        }
        transaction.commit();
    }


    public void initializeFragments() {
        fragmentLocationManager = new LocationManagerFragment();
        fragmentLocationProviderClientFragment = new FusedLocationProviderClientFragment();
    }
}