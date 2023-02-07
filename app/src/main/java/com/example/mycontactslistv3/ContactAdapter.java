package com.example.mycontactslistv3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
/*
The @NonNull notation used throughout this code indicates that the parameter—for example, public ContactViewHolder(@NonNull View itemView)
{—cannot contain a null value or, if it’s before a method, that the method cannot return a null value
 */
public class ContactAdapter extends RecyclerView.Adapter {
    //private ArrayList<String> contactData;  //Since we retrieve the contact names as an ArrayList of Strings, we must match that data type here.
    //Above was commented out as changed to

    private ArrayList<Contact> contactData;
    private View.OnClickListener mOnItemClickListener;  //This line declares a private variable to hold the OnClickListener object passed from the activity.

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        /*
        Declares the above class as a subclass of RecyclerView’s ViewHolder. Next, a variable to hold a reference to the
        only widget in the item’s layout is declared. Since the only widget is a TextView, this is all that is declared.
         */
        public TextView textViewContact;
        public TextView textPhone;
        public Button deleteButton;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContact = itemView.findViewById(R.id.textContactName);
            textPhone = itemView.findViewById(R.id.textPhoneNumber);
            deleteButton = itemView.findViewById(R.id.buttonDeleteContact);
            itemView.setTag(this); //The first line sets a tag so that we can identify which item was clicked.
            itemView.setOnClickListener(mOnItemClickListener);  // The second line sets the ViewHolder’s OnClickListener to the listener passed from the activity.
        }
        public TextView getContactTextView() {
            return textViewContact;
        }

        public TextView getPhoneTextView() {
            return textPhone;
        }

        public Button getDeleteButton() {
            return deleteButton;
        }

        }
        public ContactAdapter(ArrayList<Contact> arrayList) {
        //A constructor method for the adapter is declared.
        contactData = arrayList;                            //This constructor is used to associate the data to be displayed with the adapter.
        }


    public void setOnItemClickListener(View.OnClickListener itemClickListener) {  //This code sets up an adapter method so that we can pass the listener
            // from the activity to the adapter.
        mOnItemClickListener = itemClickListener;
        }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {    /*This is a required method for a RecyclerView.Adapter.
        // It overrides the superclasses method.
        // This method is called for each item in the data set to be displayed. Its job is to create the visual
        // display for each item using the layout file we created. For each item, a ViewHolder is created using the inflated XML and returned
        // to the RecylcerView to be displayed in the activity.
        */
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {       /*This is also a required method. It is also called
    for each item in the data set. It is passed to the ViewHolder created by the OnCreateViewHolder method as a generic ViewHolder.
    This is then cast into the associated ContactViewHolder, and the classes getContactTextView method is called to set the text attribute
    of the TextView to the name of the contact at the current position in the data set.
    */
        ContactViewHolder cvh = (ContactViewHolder) holder;
     //   cvh.getContactTextView().setText(contactData.get(position)); --Commented out to modify to below
          cvh.getContactTextView().setText(contactData.get(position).getContactName());
          cvh.getPhoneTextView().setText(contactData.get(position).getPhoneNumber());

    }


    @Override
    public int getItemCount() {         /*This is the last required method in the adapter. It returns the number of items in the data set.
    This number is used to determine how many times the other two methods need to be executed.
    */
        return contactData.size();
    }
}


