/**
 * File: ContactsFragment.java
 * Description: This program creates a fragment that will layout and implement the functionality of
 * a page that displays the users contacts.. This fragment handles the fetching and displaying of the
 * users contacts. When clicked on, the user can send a message to the desired contact containing the
 * url for the restaurant Yelp page.
 *
 */

package com.aungkhant.DashDining;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    private Activity containerActivity = null;
    private View inflatedView = null;
    private ListView contactsListView;
    ArrayAdapter<String> contactsAdapter = null;
    ArrayList<String> contactIdList = new ArrayList<>();
    private ArrayList<String> contacts = new ArrayList<String>();
    String restaurantUrl;
    String id1;
    String contactId;
    String phoneNumber;
    String newName;

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
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_contacts, container, false);

        contactsListView = inflatedView.findViewById(R.id.contact_list_view);

        Bundle bundle = getArguments();
        restaurantUrl = bundle.getString("url");

        // Set an item click listener for ListView
        contactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position);
                for (int i = 0; i < contactIdList.size(); i++) {
                    if (i == 0) {
                        id1 = contactIdList.get(0);
                    } else if (i == position) {
                        System.out.println(position);
                        id1 = contactIdList.get(i);
                    }
                }

                Cursor phones = containerActivity.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id1,
                        null, null);

                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(phones.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));

                    String name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String parts[] = name.split(" ", 2);
                    newName = parts[0];

                    Uri uri = Uri.parse("smsto:" + phoneNumber);
                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                    intent.putExtra("sms_body", "Hey " + newName + "! How does this sound for food? " + restaurantUrl);
                    startActivity(intent);
                }
                phones.close();
            }
        });

        return inflatedView;
    }

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        getContacts();
    }

    /**
     * Set up the contacts adapter when the activity lifecycle state is in onResume.
     */
    @Override
    public void onResume() {
        super.onResume();
        setupContactsAdapter();
    }

    /**
     * Get contacts from the device and display them in a ListView.
     */
    public void getContacts() {
        int limit = 1000;

        // Create cursor to access contact information and sort by alphabetical order
        Cursor cursor = containerActivity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (cursor.moveToNext() && limit > 0) {
            contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            contactIdList.add(contactId);

            // Create a phone cursor for each contact to access the contact IDs for each contact
            Cursor phones = containerActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

            // Add each contact ID to a list
            while (phones.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contact = name;

                // Skip duplicate contacts
                if (contacts.contains(contact)) {
                    continue;
                } else {
                    contacts.add(contact);
                }
                limit--;
            }
            phones.close();
        }
        cursor.close();
    }

    /**
     * Set up the adapter required to display the contacts
     */
    private void setupContactsAdapter() {
        contactsListView =
                (ListView)containerActivity.findViewById(R.id.contact_list_view);
        contactsAdapter = new
                ArrayAdapter<String>(containerActivity, R.layout.text_row,
                R.id.text_row_text_view, contacts);
        contactsListView.setAdapter(contactsAdapter);
    }
}
