/**
 * File: ExpandableListAdapter.java
 * Description: This program creates a class that extends the BaseExpandableListAdapter
 * class and creates a modified list adapter that is used to display the frequently asked
 * questions and answers in FAQFragment.java.
 *
 */

package com.aungkhant.DashDining;

import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;


public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    /**
     * This method acts as a constructor for the class.
     */
    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    /**
     * This method acts as a getter for the child element
     * @param childPosition Index of child element
     * @param groupPosition Index of group position
     * @return child object
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosition);
    }

    /**
     * This method acts as a getter for the child Id
     * @param childPosition Index of child element
     * @param groupPosition Index of group position
     * @return child Id
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * This method acts as a getter for the child view
     * @return child view
     */
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.listItem);

        txtListChild.setText(childText);
        return convertView;
    }

    /**
     * This method acts as a getter for the amount of children elements
     * @return number of children elements
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    /**
     * This method acts as a getter for the object representing the group
     * of children elements
     * @return group object
     */
    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    /**
     * This method acts as a getter for the size of the group object
     * @return size of the group
     */
    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    /**
     * This method acts as a getter for the id of the group object
     * @param groupPosition index value of group position
     * @return group Id
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * This method acts as a getter for the view representing the current group object
     * @return group view
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.listTitle);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    /**
     * This method returns true if Ids are stable, false otherwise.
     * @return boolean
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * This method returns true if he given child element is selectable, false
     * otherwise.
     * @return boolean
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}