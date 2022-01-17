/**
 * File: UsingAppFragment.java
 * Description: This program creates a fragment that will layout and implement the functionality of
 * a page that displays information regarding how to use and navigate through the application.
 *
 */

package com.aungkhant.DashDining;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class UsingAppFragment extends Fragment {

    public Activity containerActivity = null;

    /**
     * This method acts as a setter for the containerActivity field.
     * @param containerActivity Activity where fragment will be contained
     */
    public void setContainerActivity(Activity containerActivity) {
        this.containerActivity = containerActivity;
    }

    /**
     * This method initializes the fragment and populates all its elements in the given container.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_using_app, container, false);
        return v;
    }
}
