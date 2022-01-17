/**
 * File: WheelFragment.java
 * Description: This program creates a fragment that will layout and implement the functionality of
 * the wheel screen, where the user will be able to spin it, and have it continue onto the
 * decription of the restaurant selected.
 *
 */

package com.aungkhant.DashDining;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cleveroad.sy.cyclemenuwidget.CycleMenuWidget;
import com.cleveroad.sy.cyclemenuwidget.OnMenuItemClickListener;
import com.ramotion.fluidslider.FluidSlider;

import java.util.Random;

public class WheelFragment extends Fragment implements LocationListener {

    // Global constants
    private static final int SECTION_ANGLE = 45;
    private static final int WHEEL_SPIN = 3600;
    private static final Random RAND = new Random();
    private static final int NUMBER_OF_SECTIONS = 8;
    private static final String RADIUS_KEY = "radius";
    private static final int DEFAULT_RADIUS_DISTANCE = 5;
    private static final String[] WHEEL_SECTIONS = {"restaurant", "american,bbq,burgers,newamerican",
            "vegetarian,vegan", "mexican,tacos,latin",
            "chinese", "italian", "greek,mediterranean", "japanese,sushi,ramen"};
    private static final int SPIN_DURATION = 5000;

    // Class fields
    private TextView radiusTextView;
    private Activity containerActivity;
    private View view;
    private int radius = 0;
    private int currentAngle = 0;
    private int numberOfSpins = 1;
    private int currentRandomIndex;
    private LocationManager locationManager;
    private double latitude;
    private double longitude;

    // Methods

    /**
     * This method acts as a setter for the containerActivity field.
     * @param containerActivity Activity where fragment will be contained
     */
    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    /**
     * This method initializes the location manager that will be used to obtain the user's location.
     */
    @SuppressLint("MissingPermission")
    private void setUpLocation() {
        try {
            locationManager = (LocationManager) containerActivity.getSystemService
                    (Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1, 1, this);

            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method starts the animation of the wheel spinning. It does so by calculating a random
     * angle and adding it to a set angle, creating a random offset every time. Once the animation
     * ends, it launches the restaurant fragment.
     */
    public void spinWheel() {
        int finalAngle = (numberOfSpins * WHEEL_SPIN) + getRandomAngle();
        numberOfSpins ++;

        // Animating wheel
        final RotateAnimation rotate = new RotateAnimation(currentAngle, finalAngle,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setFillAfter(true);
        rotate.setFillEnabled(true);
        rotate.setDuration(SPIN_DURATION);

        rotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                new Handler(Looper.getMainLooper()).postDelayed( () -> {
                            Toast.makeText(getContext().getApplicationContext(), "Loading restaurant...",
                                    Toast.LENGTH_SHORT).show();
                            launchRestaurantFragment();
                        }, 1500
                );
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        currentAngle = finalAngle;

        final ImageView wheel = (ImageView) containerActivity.findViewById(R.id.wheel);
        wheel.startAnimation(rotate);
    }

    /**
     * This method first checks whether the screen is big or small. A Fragment Transaction
     * takes place to open the RestaurantFragment. Method is called 1.5 seconds after wheel
     * stops spinning.
     */
    private void launchRestaurantFragment() {
        int layoutId;
        if (MainActivity.isBigScreen) {
            layoutId = R.id.right_fragment_container;
        } else {
            layoutId = R.id.fragment_container;
        }
        RestaurantFragment restaurantFragment = new RestaurantFragment();
        restaurantFragment.setContainerActivity(containerActivity);
        restaurantFragment.setLayoutFile(R.layout.restaurant_fragment);
        restaurantFragment.setNewSpin();

        // Adding params
        Bundle arguments = new Bundle();
        arguments.putString(RestaurantFragment.CATEGORY_KEY, WHEEL_SECTIONS[currentRandomIndex]);
        arguments.putInt(RestaurantFragment.RADIUS_KEY, radius);
        arguments.putDouble(RestaurantFragment.LATITUDE_KEY, latitude);
        arguments.putDouble(RestaurantFragment.LONGITUDE_KEY, longitude);
        restaurantFragment.setArguments(arguments);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(layoutId, restaurantFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (!MainActivity.isBigScreen) {
            transaction.addToBackStack(null);

        }
        transaction.commit();
    }

    /**
     * This method generates a random angle, which is a multiple of a constant section angle.
     * @return
     */
    private int getRandomAngle() {
        int randomIndex = RAND.nextInt(NUMBER_OF_SECTIONS);
        currentRandomIndex = randomIndex;
        return randomIndex * SECTION_ANGLE;
    }

    /**
     * This method initializes the fragment and populates all its elements in the given container.
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.wheel_fragment, container, false);
        setUpSeekbar();
        setUpLocation();
        setUpHelpMenu();
        return view;
    }




    private  void setUpHelpMenu() {
        CycleMenuWidget cycleMenuWidget = view.findViewById(R.id.itemCycleMenuWidget);
        cycleMenuWidget.setMenuRes(R.menu.cycle_menu);
        cycleMenuWidget.setCurrentItemsAngleOffset(20);
        cycleMenuWidget.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(View view, int itemPosition) {
                onHelp(view);
            }

            @Override
            public void onMenuItemLongClick(View view, int itemPosition) {

            }
        });
    }

    /**
     * This method reads a saved preferred distance radius from the device's saved preferences.
     */
    private void getSavedPreferredRadius() {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!sharedPreferences.contains(RADIUS_KEY)) {
            editor.putInt(RADIUS_KEY, DEFAULT_RADIUS_DISTANCE);
            editor.commit();
            radius = DEFAULT_RADIUS_DISTANCE;
        } else {
            radius = sharedPreferences.getInt(RADIUS_KEY, DEFAULT_RADIUS_DISTANCE);
        }
    }



    /**
     * This method obtains the seekbar from the view and sets its attributes, as well as creates a
     * listener to obtain changes made by the user.
     */
    private void setUpSeekbar () {
        getSavedPreferredRadius();

        // Creating listener for seekbar
        final int max = 35;
        final int min = 5;
        final int total = max - min;

        final FluidSlider slider = view.findViewById(R.id.fluidSlider);

        slider.setPositionListener(pos -> {
            radius = (int) (min + total * pos);
           slider.setBubbleText(""+radius);
            return null;
        });

        slider.setEndTrackingListener(() ->{
            Toast.makeText(getActivity().getApplicationContext(), radius + " miles radius", Toast.LENGTH_SHORT  ).show();
            System.out.println("blah");
            return null;
        });


        slider.setPosition(0.2f);
        slider.setStartText(String.valueOf(min));
        slider.setEndText(String.valueOf(max));


        // Adding value from seekbar to textView

        // Assign starting value from preferences stored in device
    }

    /**
     * The following methods implement the functionality of the LocationListener interface by
     * getting changes in the user's location and storing it accordingly.
     */
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        Toast.makeText(getActivity().getApplicationContext(), "Please Enable GPS and Internet",
                Toast.LENGTH_LONG).show();
    }



    private void onHelp(View v) {
        switch (v.getId()) {
            case R.id.item_how:
                UsingAppFragment appFrag = new UsingAppFragment();
                appFrag.setContainerActivity(containerActivity);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction.replace(R.id.fragment_container, appFrag);
                transaction.addToBackStack(null);
                transaction.commit();
                break;

            case R.id.item_yelp:
                YelpAPIFragment yelpFrag = new YelpAPIFragment();
                yelpFrag.setContainerActivity(containerActivity);

                FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
                transaction2.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction2.replace(R.id.fragment_container, yelpFrag);
                transaction2.addToBackStack(null);
                transaction2.commit();
                break;

            case R.id.item_faq:
                FAQFragment faqFrag = new FAQFragment();
                faqFrag.setContainerActivity(containerActivity);

                FragmentTransaction transaction4 = getFragmentManager().beginTransaction();
                transaction4.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                transaction4.replace(R.id.fragment_container,faqFrag);
                transaction4.addToBackStack(null);
                transaction4.commit();
                break;

            default:
                break;
        }
    }
}
