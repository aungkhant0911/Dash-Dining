/**
 *
 * File: RestaurantFragment.java
 * Description: This program creates the fragment that will contain all of the restaurant information.
 *
 */

package com.aungkhant.DashDining;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

public class RestaurantFragment extends Fragment implements View.OnClickListener {
    // Global constants
    public static final double METERS_IN_MILE = 1600;

    public static final String RADIUS_KEY = "radius";
    public static final String CATEGORY_KEY = "category";
    public static final String LATITUDE_KEY = "lat";
    public static final String LONGITUDE_KEY = "lon";
    private static final String JSON_KEY = "json";

    private View view;
    private int layoutFile;
    private Activity containerActivity;
    private String url = null;
    private String phoneNumber = null;

    // Views in layout file
    private TextView restaurantTitle;
    private ImageView restaurantImage;
    private TextView restaurantReviews;
    private TextView restaurantInfo;
    private String restaurantAddress = "";
    private TextView restaurantContactInfo;
    private Bitmap restaurantImageBitmap;
    private boolean newSpin = false;
    private int layoutId;

    /**
     * This method acts as a setter for the container activity
     * @param containerActivity activity where fragment is contained.
     */
    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    /**
     * This method sets the layout file where this fragment will be inflated into.
     * @param layoutFile id of layout file.
     */
    public void setLayoutFile(int layoutFile) {
        this.layoutFile = layoutFile;
    }

    /**
     * This method indicated that when the fragment is launched, a new spin should happened, and
     * therefore, to create a new API query.
     */
    public void setNewSpin() {
        newSpin = true;
    }

    /**
     * This method places all of the elements of the fragment in the layout indicated into the
     * activity.
     */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (MainActivity.isBigScreen) {
            layoutId = R.id.right_fragment_container;
        } else {
            layoutId = R.id.fragment_container;
        }

        view = inflater.inflate(layoutFile, container, false);
        restaurantTitle = view.findViewById(R.id.restaurant_name_textview);
        restaurantImage = view.findViewById(R.id.restaurant_image);
        restaurantReviews = view.findViewById(R.id.reviews);
        restaurantInfo = view.findViewById(R.id.restaurant_info);
        restaurantContactInfo = view.findViewById(R.id.restaurant_phone_number);

        if (newSpin) {
            new YelpAPIAsyncTask().execute();
        } else {
            getSavedJSONRequest();
        }

        return view;
    }

    /**
     * This method obtains a saved json request from the device's shared preferences.
     */
    private void getSavedJSONRequest() {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!sharedPreferences.contains(JSON_KEY)) {
            editor.putString(JSON_KEY, "");
            editor.commit();
        } else {
            String json = sharedPreferences.getString(JSON_KEY, "");
            try {
                JSONObject request = new JSONObject(json);
                populateFromJSON(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method saves a json request into the device's shared preferences.
     * @param JSONString json request as a string
     */
    private void saveJSONRequest(String JSONString) {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(JSON_KEY, JSONString);
        editor.commit();
    }

    /**
     * This method initializes all of the elements in the fragment, and saves them as class variables.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // All the buttons are set to click listeners
        Button directions = getActivity().findViewById(R.id.directions_button);
        directions.setOnClickListener(this);
        Button yelpButton = getActivity().findViewById(R.id.yelp_button);
        yelpButton.setOnClickListener(this);
    }

    /**
     * This method converts miles into meters
     * @param miles amount in miles to be converted
     * @return double indicating miles in meters
     */
    public int getMetersFromMiles(double miles) {
        return (int)Math.floor(miles * METERS_IN_MILE);
    }

    /**
     * This method handles the onClick functionality for the three buttons displayed on the RestaurantFragment
     * @param v current View object when buttons are clicked
     */
    @Override
    public void onClick(View v) {
        newSpin = false;
        switch (v.getId()) {

            case R.id.directions_button:
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + restaurantAddress);
                Intent directionsIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                directionsIntent.setPackage("com.google.android.apps.maps");
                startActivity(directionsIntent);


//                Uri gmmIntentUri = Uri.parse("https://www.google.co.in/maps/dir/18.6121132,73.707989/18.5,73.7/18.8,73.71");
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//
//                startActivity(mapIntent);
                break;

            case R.id.yelp_button:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
    }

    /**
     * This method initializes a phone call intent.
     * @param v view from where it was called.
     */
    public void callRestaurant(View v) {
        Uri phoneUri = Uri.parse("tel:" + phoneNumber);
        Intent phoneCallIntent = new Intent(Intent.ACTION_CALL, phoneUri);
        startActivity(phoneCallIntent);
    }

    /**
     * This method populates all the restaurant information into the fragment from the information
     * contained in the request as a JSON object.
     * @param chosenRestaurant restaurant's JSON object request
     */
    public void populateFromJSON(final JSONObject chosenRestaurant) {
        try {
            String restaurantName = chosenRestaurant.getString("name");
            String displayPhoneNumber = chosenRestaurant.getString("display_phone");
            url = chosenRestaurant.getString("url");

            JSONObject location = (JSONObject)chosenRestaurant.getJSONObject("location");
            JSONArray address = (JSONArray)location.getJSONArray("display_address");
            restaurantAddress = address.get(0) + ", " + address.get(1);
            phoneNumber = chosenRestaurant.getString("phone");

            String info = "Price: " + chosenRestaurant.getString("price") + "    |    " +
                    "Rating: " + chosenRestaurant.getString("rating") + " \uD83C\uDF1F" +
                    "\n" + restaurantAddress;

            new YelpAPIReviews().execute(chosenRestaurant.getString("id"));
            restaurantTitle.setText(restaurantName);
            restaurantContactInfo.setText(displayPhoneNumber);
            restaurantInfo.setText(info);

            // Pulling image
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        restaurantImageBitmap = getBitmapFromURL(chosenRestaurant
                                .getString("image_url"));
                        containerActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                restaurantImage.setImageBitmap(restaurantImageBitmap);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
//            restaurantImage.setImageBitmap(restaurantImageBitmap);
        } catch (JSONException e) {
            new YelpAPIAsyncTask().execute();
        }
    }

    /**
     * This method downloads an image from a given url.
     * @param src url pointing to the image
     * @return image Bitmap
     */
    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This class creates an API call to get a restaurant's list of reviews.
     */
    private class YelpAPIReviews extends AsyncTask<String, Void, JSONObject> {
        private Uri.Builder uri = new Uri.Builder().scheme("https").authority("api.yelp.com")
                .appendPath("v3").appendPath("businesses");

        /**
         * This method processes the API query
         * @param id restaurant id
         * @return restaurant's reviews in JSON format
         */
        @Override
        protected JSONObject doInBackground(String... id) {
            uri.appendPath(id[0]);
            uri.appendPath("reviews");

            try {
                URL url = new URL(uri.toString());
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Bearer " + YelpAPICrenditials.API_KEY);

                String json = "";
                String line;
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = in.readLine()) != null) {
                    json += line;
                }
                in.close();
                JSONObject request = new JSONObject(json);


                return request;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * This method populates the given restaurant's reviews.
         */
        @Override
        protected void onPostExecute(JSONObject requestJSON) {
            String reviewText = "Reviews: \n";
            try {
                JSONArray reviews = requestJSON.getJSONArray("reviews");
                if (reviews.length() > 0) {
                    JSONObject tempReview = (JSONObject) reviews.get(0);
                    reviewText += tempReview.getString("text") + "\n";
                } else {
                    reviewText += "No reviews yet";
                }

                restaurantReviews.setText(reviewText);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * This class creates an API call to obtain a list of restaurants within the given parameters.
     */
    private class YelpAPIAsyncTask extends AsyncTask<String, Void, JSONObject> {
        // Fields
        private Uri.Builder uri = new Uri.Builder().scheme("https").authority("api.yelp.com")
                .appendPath("v3").appendPath("businesses").appendPath("search")
                .appendQueryParameter("open_now", "true").appendQueryParameter("sort_by", "rating");

        /**
         * This method queries the news API based on the keyword given.
         * @param query keyword to be used to query API
         * @return JSON object containing query
         */
        @Override
        protected JSONObject doInBackground(String... query) {
            String lat =  String.valueOf(getArguments().getDouble(LATITUDE_KEY));
            String lon = String.valueOf(getArguments().getDouble(LONGITUDE_KEY));
            String radius = String.valueOf(getMetersFromMiles(getArguments().getInt(RADIUS_KEY)));
            String categoryParam = getArguments().getString(CATEGORY_KEY);

            Random random = new Random();

            uri.appendQueryParameter("categories", categoryParam);
            uri.appendQueryParameter("latitude", lat);
            uri.appendQueryParameter("longitude", lon);
            uri.appendQueryParameter("radius", radius);
            uri.appendQueryParameter("limit", "10");


            try {
                URL url = new URL(uri.toString());
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Bearer " + YelpAPICrenditials.API_KEY);

                String json = "";
                String line;
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = in.readLine()) != null) {
                    json += line;
                }
                in.close();
                JSONObject request = new JSONObject(json);

                JSONArray businesses = request.getJSONArray("businesses");

                // Check if no results
                if (businesses.length() <= 0)
                    return null;


                JSONObject chosenRestaurant = (JSONObject) businesses
                        .get(random.nextInt(businesses.length()));

                return chosenRestaurant;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        /**
         * This method calls the method to populate text fields and images in the view from the
         * given JSON request.
         */
        @Override
        protected void onPostExecute(JSONObject chosenRestaurant) {
            if(chosenRestaurant == null) {
                Toast.makeText(getActivity().getApplicationContext(), "Can't find such restaurant in vincinity. Please try again!", Toast.LENGTH_LONG).show();
               // getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                return;
            }

            saveJSONRequest(chosenRestaurant.toString());
            populateFromJSON(chosenRestaurant);
        }

    }
}
