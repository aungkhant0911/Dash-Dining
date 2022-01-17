/**
 *
 * File: FAQFragment.java
 * Description: This program creates a fragment that will layout and implement the functionality of
 * the frequently asked question screen. The fragment utilizes an expandable list adapter to create
 * a UI in which the user can view frequently asked questions and their corresponding answers.
 *
 */

package com.aungkhant.DashDining;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FAQFragment extends Fragment {

    public Activity containerActivity = null;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

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
        View v = inflater.inflate(R.layout.fragment_faq, container, false);

        return v;
    }

    /**
     * This method is called once the view has been created and initialized.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        expListView = view.findViewById(R.id.expListView);

        // Prepare list data by adding questions and answers to lists
        prepareListData();

        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // Set the list adapter
        expListView.setAdapter(listAdapter);
    }

    /**
     * This method adds the questions and answers data to lists in order for the
     * data to be displayed onto the UI using the ExpandableListAdapter.java
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding question data
        listDataHeader.add(getResources().getString(R.string.question1));
        listDataHeader.add(getResources().getString(R.string.question2));
        listDataHeader.add(getResources().getString(R.string.question3));

        // Adding answer data
        List<String> question1 = new ArrayList<String>();
        question1.add(getResources().getString(R.string.answer1));

        List<String> question2 = new ArrayList<String>();
        question2.add(getResources().getString(R.string.answer2));

        List<String> question3 = new ArrayList<String>();
        question3.add(getResources().getString(R.string.answer3));

        listDataChild.put(listDataHeader.get(0), question1);
        listDataChild.put(listDataHeader.get(1), question2);
        listDataChild.put(listDataHeader.get(2), question3);
    }
}