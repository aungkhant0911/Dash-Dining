/**
 *
 * File: MainActivity.java
 * Description: This program creates the app's main activity where all the fragments will be
 * displayed. It also gets events from buttons and redirects them accordingly/
 *
 */

package com.aungkhant.DashDining;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS_REQUIRED = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    public static boolean isBigScreen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupPermissions();

//        if (isBigScreen()) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            isBigScreen = true;
//            setUpFragmentsForBigScreen();
//
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//           setUpFragmentsForSmallScreen();
//        }

        setUpFragmentsForSmallScreen();

    }



    /**
     * This method requests the user all the permissions required by the application.
     */
    private void setupPermissions() {
        List<String> permissionsToRequest = new ArrayList<String>();
        for (String permission : PERMISSIONS_REQUIRED) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        String[] permissions = new String[permissionsToRequest.size()];
        permissions = permissionsToRequest.toArray(permissions);

        if (permissions.length > 0)
            ActivityCompat.requestPermissions(this, permissions, 101);
    }

    /**
     * This method is called when the spin button is clicked, and it calls the spin method in the
     * Wheel fragment.
     * @param v view from which method was called.
     */
    public void spinWheel(View v) {
        if (isBigScreen) {
            WheelFragment wheelFragment = (WheelFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.left_fragment_container);
            wheelFragment.spinWheel();
        } else {
            WheelFragment wheelFragment = (WheelFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);
            wheelFragment.spinWheel();
        }
    }

    /**
     * This method initializes a phone call intent.
     * @param v view from which the method was called.
     */
    public void callRestaurant(View v) {
        RestaurantFragment restaurantFragment = (RestaurantFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        restaurantFragment.callRestaurant(v);
    }

    /**
     * This method performs a fragment transaction to open the initial screen WheelFragment
     * for devices with "small" (less than 5 by 5 inch) screens.
     */
    private void setUpFragmentsForSmallScreen() {
        setContentView(R.layout.activity_main);
        WheelFragment wheelFragment = new WheelFragment();
        wheelFragment.setContainerActivity(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, wheelFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * This method performs a fragment transaction to open the initial screen WheelFragment
     * for devices with "big" (larger than 5 by 5 inch) screens.
     */
    private void setUpFragmentsForBigScreen() {
        setContentView(R.layout.activity_main_big);
        WheelFragment wheelFragment = new WheelFragment();
        wheelFragment.setContainerActivity(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.left_fragment_container, wheelFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * @return true if the screen is larger than 5 by 5 inches, false otherwise
     */
    private boolean isBigScreen() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float xInches = (((float)metrics.widthPixels) / metrics.xdpi);
        float yInches = (((float)metrics.heightPixels) / metrics.xdpi);
        return xInches > 5.0 && yInches > 5.0;
    }
}
