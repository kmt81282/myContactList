package com.example.mycontactslistv3;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.SaveDateListener {

    private Contact currentContact; //Step 1 - We need an association between the MainActivity class and contact object
    //Step 2 - Associates currentContact with var with a new Contact object
    final int PERMISSION_REQUEST_PHONE = 102;
    final int PERMISSION_REQUEST_CAMERA = 103;
    final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initListButton();
        initMapButton();
        initSettingsButton();
        initToggleButton();
        initCallFunction();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            initContact(extras.getInt("contactID"));
        }
        else {
            currentContact = new Contact();
        }
        setForEditing(false);
        initChangeDateButton();
        initTextChangeEvents();
        initSaveButton();
        initImageButton();



        //currentContact = new Contact();  //deleted per 6.10 activity
    }

    // Image Button initialization for Nav Bar Contacts List
    private void initListButton() {
        ImageButton ibList = findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initToggleButton() {
        final ToggleButton editToggle = (ToggleButton) findViewById(R.id.toggleButtonEdit);
        editToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setForEditing(editToggle.isChecked());
            }
        });
    }

    private void setForEditing(boolean enabled) {

        ImageButton picture = findViewById(R.id.imageContact);
        EditText editName = findViewById(R.id.editName);
        EditText editAddress = findViewById(R.id.editAddress);
        EditText editCity = findViewById(R.id.editCity);
        EditText editState = findViewById(R.id.editState);
        EditText editZipCode = findViewById(R.id.editZipcode);
        EditText editPhone = findViewById(R.id.editHome);
        EditText editCell = findViewById(R.id.editCell);
        EditText editEmail = findViewById(R.id.editEMail);
        Button buttonChange = findViewById(R.id.btnBirthday);
        Button buttonSave = findViewById(R.id.btnSave);

        editName.setEnabled(enabled);
        editAddress.setEnabled(enabled);
        editCity.setEnabled(enabled);
        editState.setEnabled(enabled);
        editZipCode.setEnabled(enabled);
        picture.setEnabled(enabled);
    //    editPhone.setEnabled(enabled);
    //    editCell.setEnabled(enabled);
        editEmail.setEnabled(enabled);
        buttonChange.setEnabled(enabled);
        buttonSave.setEnabled(enabled);


        if (enabled) {
            editName.requestFocus();
            editPhone.setInputType(InputType.TYPE_CLASS_PHONE);
            editCell.setInputType(InputType.TYPE_CLASS_PHONE);
        } else {
            ScrollView s = findViewById(R.id.scrollView);
            s.fullScroll(ScrollView.FOCUS_UP);
            editPhone.setInputType(InputType.TYPE_NULL);
            editCell.setInputType(InputType.TYPE_NULL);
        }
    }

    private void initImageButton() {
        ImageButton ib = findViewById(R.id.imageContact);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CAMERA)) {
                            Snackbar.make(findViewById(R.id.activity_main), "The app needs permission to take pictures", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                                        }
                                    })
                                    .show();
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},
                            PERMISSION_REQUEST_CAMERA);

                        }
                    }
                    else {
                        takePhoto();
                    }
                } else {
                    takePhoto();
                }
            }
        });
    }

    //Image Button initialization for nav bar Mapping
    private void initMapButton() {
        ImageButton ibList = findViewById(R.id.imageButtonMap);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactMapActivity.class);
                if (currentContact.getContactID() == -1) {
                    Toast.makeText(getBaseContext(),"Contact must be saved before it can be mapped",Toast.LENGTH_LONG).show();
                }
                else {
                    intent.putExtra("contactid",currentContact.getContactID());
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    //Image Button initialization for Nav Bar Settings
    private void initSettingsButton() {
        ImageButton ibList = findViewById(R.id.imageButtonSettings);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public void didFinishDatePickerDialog(Calendar selectedTime) {
        TextView birthDay = findViewById(R.id.textBirthday);
        birthDay.setText(DateFormat.format("MM/dd/yyyy", selectedTime));
        currentContact.setBirthday(selectedTime);  //Step 3: added code to store the select birthday in the Contact object
    }

    private void initChangeDateButton() {
        Button changeDate = findViewById(R.id.btnBirthday);
        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerDialog datePickerDialog = new DatePickerDialog();
                datePickerDialog.show(fm, "DatePick");
            }
        });
    }

    private void initTextChangeEvents() {
        final EditText etContactName = findViewById(R.id.editName);  //var declared final because it is used inside the event
        etContactName.addTextChangedListener(new TextWatcher() {
            /*
            new TextWatcher object. TextWatcher is an object that, when attached to a widget that allows editing, will execute its methods when the text in the widget is changed.
            The TextWatcher object requires that three methods (lines 3, 5, and 6) are implemented, even though you will use only one of these events.
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*
                The beforeTextChanged method is a required TextWatcher method. This method is executed when
                the user presses down on a key to enter it into an EditText but before the value in the
                EditText is actually changed.
                 */

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*
                The onTextChanged method is also a required TextWatcher method. The method is executed
                after each and every character change in an EditText.
                 */

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setContactName(etContactName.getText().toString());
                /*
                Called after the user completes editing the data and leaves the EditText. This is the event that this app uses to capture the data the user entered.
                currentContact.set happens after user move out of the field after changing information. The new info is converting it to a string
                 */

            }
        });

        final EditText etStreetAddress = findViewById(R.id.editAddress);
        etStreetAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setStreetAddress(etStreetAddress.getText().toString());

            }
        });
        final EditText etCity = findViewById(R.id.editCity);
        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setCity(etCity.getText().toString());

            }
        });
        final EditText etState = findViewById(R.id.editState);
        etState.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setState(etState.getText().toString());

            }
        });
        final EditText etHomePhone = findViewById(R.id.editHome);
        etHomePhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                currentContact.setPhoneNumber(etHomePhone.getText().toString());


            }
        });
        final EditText etCellPhone = findViewById(R.id.editCell);
        etCellPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setCellNumber(etCellPhone.getText().toString());


            }
        });
        final EditText etEmail = findViewById(R.id.editEMail);
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.seteMail(etEmail.getText().toString());

            }
        });
        final EditText etZip = findViewById(R.id.editZipcode);
        etZip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                currentContact.setZipCode(etZip.getText().toString());

            }
        });
        etHomePhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        etCellPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());


    }

    private void initSaveButton() {
        Button saveButton = findViewById(R.id.btnSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                boolean wasSuccessful = true;
                ContactDataSource ds = new ContactDataSource(MainActivity.this);
                try {
                    ds.open();

                    if (currentContact.getContactID() == -1) {

                        wasSuccessful = ds.insertContact(currentContact);
                        if (wasSuccessful) {

                            int newID = ds.getLastContactID();
                            currentContact.setContactID(newID);
                        }
                    } else {
                        wasSuccessful = ds.updateContact(currentContact);
                    }
                    ds.close();
                } catch (Exception e) {
                    wasSuccessful = false;
                }
                if (wasSuccessful) {
                    ToggleButton editToggle = findViewById(R.id.toggleButtonEdit);
                    editToggle.toggle();
                    setForEditing(false);
                }
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText editName = findViewById(R.id.editName);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
        EditText editAddress = findViewById(R.id.editAddress);
        imm.hideSoftInputFromWindow(editAddress.getWindowToken(), 0);
        EditText editCity = findViewById(R.id.editCity);
        imm.hideSoftInputFromWindow(editCity.getWindowToken(), 0);
        EditText editState = findViewById(R.id.editState);
        imm.hideSoftInputFromWindow(editState.getWindowToken(), 0);
        EditText editZipcode = findViewById(R.id.editZipcode);
        imm.hideSoftInputFromWindow(editZipcode.getWindowToken(), 0);
        EditText editHome = findViewById(R.id.editHome);
        imm.hideSoftInputFromWindow(editHome.getWindowToken(), 0);
        EditText editCell = findViewById(R.id.editCell);
        imm.hideSoftInputFromWindow(editCell.getWindowToken(), 0);
        EditText editMail = findViewById(R.id.editEMail);
        imm.hideSoftInputFromWindow(editMail.getWindowToken(), 0);

    }

    private void initContact(int id) {
        ContactDataSource ds = new ContactDataSource(MainActivity.this);
        try {
            ds.open();
            currentContact = ds.getSpecificContact(id);
            ds.close();
        } catch (Exception e) {
            Toast.makeText(this, "Load Conatct Failed", Toast.LENGTH_LONG).show();
        }

        EditText editName = findViewById(R.id.editName);
        EditText editAddress = findViewById(R.id.editAddress);
        EditText editCity = findViewById(R.id.editCity);
        EditText editState = findViewById(R.id.editState);
        EditText editZipcode = findViewById(R.id.editZipcode);
        EditText editPhone = findViewById(R.id.editHome);
        EditText editCell = findViewById(R.id.editCell);
        EditText editMail = findViewById(R.id.editEMail);
        TextView birthDay = findViewById(R.id.textBirthday);
        ImageButton picture = (ImageButton) findViewById(R.id.imageContact);

        editName.setText(currentContact.getContactName());
        editAddress.setText(currentContact.getStreetAddress());
        editCity.setText(currentContact.getCity());
        editState.setText(currentContact.getState());
        editZipcode.setText(currentContact.getZipCode());
        editPhone.setText(currentContact.getPhoneNumber());
        editCell.setText(currentContact.getCellNumber());
        editMail.setText(currentContact.geteMail());
        birthDay.setText(DateFormat.format("MM/dd/yyyy",
                currentContact.getBirthday().getTimeInMillis()).toString());
        if (currentContact.getPicture() != null) {
            picture.setImageBitmap(currentContact.getPicture());
        }
        else {
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.photo);
            picture.setImageBitmap(b);
        }
    }
    private void initCallFunction() {
        EditText editPhone = (EditText) findViewById(R.id.editHome);
        editPhone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkPhonePermission(currentContact.getPhoneNumber());
                return false;
            }
        });

        EditText editCell = (EditText) findViewById(R.id.editCell);
        editCell.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkPhonePermission(currentContact.getCellNumber());
                return false;
            }
        });
    }


    private void checkPhonePermission(String phoneNumber) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        android.Manifest.permission.CALL_PHONE)) {
                    Snackbar.make(findViewById(R.id.activity_main),
                                    "MyContactList requires this permission to place a call from the app.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                            android.Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_PHONE);
                                }
                            })
                            .show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                            PERMISSION_REQUEST_PHONE);
                }
            } else {
                callContact(phoneNumber);
            }
        } else {
            callContact(phoneNumber);
        }
    }

    @Override
    public void onReqestPermissionResult(int requestCode, @NonNull String permission[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "You may now call from this app.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "You will not be able to make calls" +
                            "from this app", Toast.LENGTH_LONG).show();
                }
            }
            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(MainActivity.this,"You will not be able to save" +
                            "contact pictures from this app", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void callContact(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            startActivity(intent);
        }
    }

    public void takePhoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, 144, 144, true);
                ImageButton imageContact = (ImageButton) findViewById(R.id.imageContact);
                imageContact.setImageBitmap(scaledPhoto);
                currentContact.setPicture(scaledPhoto);
            }
        }
    }

}





