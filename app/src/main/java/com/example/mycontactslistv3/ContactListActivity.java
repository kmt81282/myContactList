package com.example.mycontactslistv3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {

    RecyclerView contactList;
    ArrayList<Contact> contacts;
    ContactAdapter contactAdapter = new ContactAdapter(contacts);

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            int contactID = contacts.get(position).getContactID();
            Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
            intent.putExtra("contactID", contactID);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        //initListButton();
        initMapButton();
        initSettingsButton();
        contactAdapter.setOnItemClickListener(onItemClickListener);


        ContactDataSource ds = new ContactDataSource(this);
        ArrayList<Contact> contacts;

        try {
            ds.open();
            //names = ds.getContactName();  //updated to below to address the contact object
            contacts = ds.getContacts();
            ds.close();
            /*
            Sets up the RecyclerView to display the data. First a reference to the widget is created using findViewById. The next line creates an instance of the
            LayoutManager used to display the individual items. In this case, we use a LinearLayoutManager to display a vertical scrolling list.
            This LayoutManager is then associated with the RecyclerView. Line 9 instantiates the ContactAdapter object you coded, passing it the ArrayList
            of contact names. Finally, this adapter is associated with the RecyclerView.
             */
            RecyclerView contactList = findViewById(R.id.rvContacts);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            contactList.setLayoutManager(layoutManager);
            ContactAdapter contactAdapter = new ContactAdapter(contacts);
            contactList.setAdapter(contactAdapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error retrieving contacts", Toast.LENGTH_LONG).show();
            /*
            The final new line is in the catch statement. A Toast displays a message on the device screen for a brief time and then goes away.
            The method makeText configures the message. The first parameter indicates where the message should display.
            In this case, we want it in the current activity (this). The second sets the message, and the third indicates how long the message
            should display. Finally, .show() displays the message.
             */

        }
    }

    private void initSettingsButton() {
        ImageButton ibList = findViewById(R.id.imageButtonSettings);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ContactListActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initMapButton() {
        ImageButton ibList = findViewById(R.id.imageButtonMap);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ContactListActivity.this, ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}

/*
    private void initListButton() {
        ImageButton ibList = findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ContactListActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}

 */





