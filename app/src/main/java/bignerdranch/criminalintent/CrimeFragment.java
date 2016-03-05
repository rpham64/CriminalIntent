package bignerdranch.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Rudolf on 2/8/2016.
 */
public class CrimeFragment extends Fragment {

    // TAG for CrimeFragment
    private static final String TAG = "CrimeFragment";

    // KEY for contactID
    private static final String KEY_ID = "id";

    // KEY for contactNumber
    private static final String KEY_NUMBER = "number";

    // Fragment argument for crime_id
    private static final String ARG_CRIME_ID = "crime_id";

    // Tag for DatePickerFragment
    private static final String DIALOG_DATE = "DialogDate";

    // Tag for TimePickerFragment
    private static final String DIALOG_TIME = "DialogTime";

    // Request code for Date (sent by DatePickerFragment)
    private static final int REQUEST_DATE = 0;

    // Request code for Time (sent by TimePickerFragment)
    private static final int REQUEST_TIME = 1;

    // Request code for Contact
    private static final int REQUEST_CONTACT = 2;

    // Request code for Photo
    private static final int REQUEST_PHOTO = 3;

    // Unique Contact ID
    private String contactID;

    // Unique Contact Phone Number
    private String contactNumber;

    private Crime mCrime;                   // Crime
    private File mPhotoFile;                // Photo File Location
    private EditText mTitleField;           // Title of Crime
    private ImageView mPhotoView;           // Photo of Crime
    private ImageButton mPhotoButton;       // Take Picture of Crime
    private Button mDateButton;             // Date of Crime
    private Button mTimeButton;             // Time of Crime
    private CheckBox mSolvedCheckBox;       // Crime solved?
    private Button mSuspectButton;          // Suspect name
    private Button mCallSuspectButton;      // Call Suspect
    private Button mReportButton;           // Send Crime report

    /**
     * Given a UUID, creates a fragment instance and attaches an arguments bundle
     * to the fragment.
     *
     * @return fragment with arguments bundle
     */
    public static CrimeFragment newInstance(UUID crimeID) {

        // Create arguments bundle. Add ARG_CRIME_ID and given UUID as key-value pair
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeID);

        // Create fragment instance
        CrimeFragment fragment = new CrimeFragment();

        // Set arguments of fragment to bundle
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Saves fragment state when fragment is destroyed
     *
     * @param key
     * @param contactNumber
     */
    public void savePreferences(String key, String contactNumber) {
        SharedPreferences sharedPreferences = getActivity().getPreferences(0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NUMBER, contactNumber);
        editor.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        Log.d(TAG, "On savedInstanceState");

        savedInstanceState.putString(KEY_NUMBER, contactNumber);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "Is savedInstanceState null?");

        if (savedInstanceState != null) {
            contactNumber = savedInstanceState.getString(KEY_NUMBER);
            Log.d(TAG, "savedInstanceState is NOT NULL");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "On Create");

        // Retrieve the UUID from fragment arguments
        UUID crimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        // Fetch the Crime object using the UUID
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeID);

        // Fetch location for pictures from CrimeLab
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

        // Create Options menu (toolbar)
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "On Start");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "On Resume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "On Pause");

        // Updates CrimeLab's copy of mCrime
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "On Stop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "On Destroy");
    }

    /**
     * Instantiates View for CrimeFragment fragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            contactID = savedInstanceState.getString(KEY_ID);
            contactNumber = savedInstanceState.getString(KEY_NUMBER);
        }

        Log.d(TAG, "On CreateView");
        Log.d(TAG, "Contact ID: " + contactID);
        Log.d(TAG, "Contact Number: " + contactNumber);

        // Inflates the layout "fragment_crime.xml"
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        // For checking if an app is unavailable
        PackageManager packageManager = getActivity().getPackageManager();

        createOrUpdatePhotoView(view);                  // Photo View
        createPhotoButton(view, packageManager);        // Photo Button

        createTitleField(view);                         // Title Field
        createDateButton(view);                         // Date Button
        createTimeButton(view);                         // Time Button
        createSolvedCheckbox(view);                     // Solved Checkbox
        createSuspectButton(view, packageManager);      // Suspect Button
        createCallButton(view);                         // Call Suspect Button
        createReportButton(view);                       // Report Button

        return view;
    }

    private void createTitleField(View view) {

        // Get reference to Title field (inflate widget)
        mTitleField = (EditText) view.findViewById(R.id.crime_title);

        // Set field's title to Crime's title
        mTitleField.setText(mCrime.getTitle());

        // Add a listener for Title field
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing here
            }
        });
    }

    private void createOrUpdatePhotoView(View view) {

        // Get reference to photo view
        mPhotoView = (ImageView) view.findViewById(R.id.crime_photo);

        // Check: mPhotoFile is null
        // If so, set mPhotoView's drawable to null
        // Else, Create a scaled bitmap and set it to mPhotoView
        if (mPhotoFile == null || !mPhotoFile.exists()) {

            mPhotoView.setImageDrawable(null);

        } else {

            final Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());

            mPhotoView.setImageBitmap(bitmap);

            Log.d(TAG, "Photo Path: " + mPhotoFile.getPath());

        }

    }

    private void createPhotoButton(View view, PackageManager packageManager) {
        // Get reference to camera button
        mPhotoButton = (ImageButton) view.findViewById(R.id.crime_camera);

        // Create an intent with action ACTION_IMAGE_CAPTURE
        // This implicit intent takes a thumbnail-sized picture using the camera app
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check: A camera app exists AND mPhotoFile is not null
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;

        // Disable camera button if canTakePhoto is false. Else, set enabled.
        mPhotoButton.setEnabled(canTakePhoto);

        // If canTakePhoto is true, read in the Uri from mPhotoFile and
        // Add an extra to captureImage with MediaStore.EXTRA_OUTPUT
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        // Create listener for mPhotoButton
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
    }

    private void createDateButton(View view) {

        // Get reference to Date Button
        mDateButton = (Button) view.findViewById(R.id.crime_date);

        // Update DateButton's text
        updateDate();

        // Create listener for Date Button
        mDateButton.setOnClickListener(new View.OnClickListener() {

            /**
             * On Date Button click, get hosting activity's FragmentManager, create
             * new DatePickerFragment instance using newInstance, and show
             * DatePickerFragment as a dialog
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                editDateButton();
            }
        });
    }

    private void createTimeButton(View view) {

        // Get reference to Time Button
        mTimeButton = (Button) view.findViewById(R.id.crime_time);

        // Update DateButton's text
        updateTime();

        // Create listener for Time Button
        mTimeButton.setOnClickListener(new View.OnClickListener() {

            /**
             * On Time Button click, get hosting activity's FragmentManager, create
             * new TimePickerFragment instance using newInstance, and show
             * TimePickerFragment as a dialog
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                editTimeButton();
            }
        });
    }

    private void createSolvedCheckbox(View view) {

        // Get reference to Solved CheckBox
        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);

        // Set Checked status
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        // Add a listener for Solved CheckBox
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Set crime's solved property
                mCrime.setSolved(isChecked);
            }
        });
    }

    private void createSuspectButton(View view, PackageManager packageManager) {

        // Create Implicit Intent with action PICK and location Content URI
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        // Get reference to Suspect Button and create its listener
        mSuspectButton = (Button) view.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Calls Implicit intent on second activity and retrieves the result
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        // Set text of Suspect Button to suspect, if it exists
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        // If no contact app exists, disable Suspect button
        if (packageManager.resolveActivity(pickContact,
                packageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }
    }

    private void createCallButton(View view) {

        // Get reference to Call Button and create its listener
        mCallSuspectButton = (Button) view.findViewById(R.id.crime_call_suspect);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Opens phone app and dials suspect's Contact Number
             *
             * @param v
             */
            @Override
            public void onClick(View v) {

                // Retrieve contact number from sharedPreferences
                SharedPreferences sharedPreferences = getActivity().getPreferences(0);
                contactNumber = sharedPreferences.getString(KEY_NUMBER, null);

                // Dial contact number using phone app
                String uri = "tel:" + contactNumber.trim();
                Uri number = Uri.parse(uri);
                Intent callNumber = new Intent(Intent.ACTION_DIAL, number);

                startActivity(callNumber);
            }
        });
    }

    private void createReportButton(View view) {

        // Get reference to Report Button and create its listener
        mReportButton = (Button) view.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Creates and Sends Implicit Intent
             *
             * @param v
             */
            @Override
            public void onClick(View v) {

                // Build Intent using ShareCompat.IntentBuilder
                ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(getActivity());

                intentBuilder.setChooserTitle(R.string.send_report)
                        .setType("text/plain")
                        .setSubject(getString(R.string.crime_report_suspect))
                        .setText(getCrimeReport());

                // Create chooser intent and send to OS
                Intent intent = Intent.createChooser(
                        intentBuilder.getIntent(),
                        getString(R.string.send_report));

                startActivity(intent);

            }
        });
    }

    /**
     * Creates four strings and pieces them together, then returns a complete report
     *
     * 1) solvedString - Displays whether mCrime is solved or not
     * 2) dateString - Displays date of mCrime
     * 3) suspect - Displays whether there is a suspect or not
     * 4) report - Completed report
     *
     * @return
     */
    private String getCrimeReport() {

        /** solvedString */
        String solvedString = null;

        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        /** dateString */
        String dateString = formatDate(mCrime.getDate());

        /** suspect */
        String suspect = mCrime.getSuspect();

        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        /** report */
        String report = getString(R.string.crime_report, mCrime.getTitle(),
                dateString, solvedString, suspect);

        return report;
    }

    /**
     * Creates Toolbar Menu.
     *
     * Contains "Delete" action item.
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    /**
     * Given a MenuItem, implements modifications to CrimeFragment.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Case: MenuItem is "Delete"
            case (R.id.menu_item_delete_crime):

                // Remove crime from CrimeLab
                CrimeLab.get(getActivity()).deleteCrime(mCrime);

                // Display toast with message "Crime deleted."
                Toast toast = Toast.makeText(getActivity(), R.string.toast_crime_deleted, Toast.LENGTH_SHORT);

                TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
                if (textView != null) {
                    textView.setGravity(Gravity.CENTER);
                }

                toast.show();

                // Close hosting activity
                getActivity().finish();

                return true;

            default:

                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Given a requestCode, resultCode and Intent, retrieve and use the extra
     * from the Intent.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check: resultCode is OK or not
        if (resultCode != Activity.RESULT_OK) return;

        // Contact URI
        Uri contactUri;

        switch (requestCode) {

            case (REQUEST_DATE):

                // Retrieve the extra (Date) from the Intent
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);

                // Set the Crime object's date to this Date
                mCrime.setDate(date);

                // Update DateButton's text
                updateDate();

                break;

            case (REQUEST_TIME):

                // Retrieve the extra (time) from the Intent
                Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);

                // Set the Crime object's time to this time
                mCrime.setDate(time);

                // Update TimeButton's text
                updateTime();

                break;

            case (REQUEST_CONTACT):

                // Check: data is null
                if (data == null) return;

                // Set contactURI to data
                contactUri = data.getData();

                retrieveContactName(contactUri);
                retrieveContactNumber(contactUri);

                break;

            case (REQUEST_PHOTO):

                createOrUpdatePhotoView(getView());

        }

    }

    private void retrieveContactName(Uri contactUri) {

        // Store field for Contact name
        String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};

        // Perform query using contactUri as "whereClause"
        Cursor cursor = getActivity().getContentResolver()
                .query(contactUri, queryFields, null, null, null);

        try {

            // Check: results not null
            if (cursor.getCount() == 0) return;

            // Pull out suspect name data from first row, first column
            cursor.moveToFirst();
            String suspect = cursor.getString(0);
            mCrime.setSuspect(suspect);
            mSuspectButton.setText(suspect);

        } finally {
            cursor.close();                 // To avoid Cursor exceptions
        }

        Log.d(TAG, "Contact Name: " + mCrime.getSuspect());
    }

    private void retrieveContactNumber(Uri contactUri) {

        String selection;
        String[] selectionArgs;

        // Store field for Contact number
        String[] queryFields = new String[] {ContactsContract.Contacts._ID};

        // Perform query using contactUri as "whereClause"
        Cursor cursorID = getActivity().getContentResolver()
                .query(contactUri, queryFields, null, null, null);

        try {

            if (cursorID.moveToFirst()) {

                int columnIndex = cursorID.getColumnIndex(ContactsContract.Contacts._ID);
                contactID = cursorID.getString(columnIndex);

            }

        } finally {
            cursorID.close();                 // To avoid Cursor exceptions
        }

        Log.d(TAG, "Contact ID: " + contactID);

        // Use contactID to retrieve contact phone number
        contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        queryFields = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER};
        selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        selectionArgs = new String[] {contactID};

        Cursor cursorPhone = getActivity().getContentResolver().query(
                contactUri, queryFields, selection, selectionArgs, null);

        try {

            if (cursorPhone.moveToFirst()) {

                int columnIndex = cursorPhone.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER);
                contactNumber = cursorPhone.getString(columnIndex);

            }

        } finally {
            cursorPhone.close();
        }

        savePreferences(KEY_NUMBER, contactNumber);

        Log.d(TAG, "Contact Phone Number: " + contactNumber);

    }
/*
    *//**
     * Given contact name, returns his phone number as a string.
     *
     * @param name
     * @return
     *//*
    private String getPhoneNumber(String name) {

        String number = null;
        Uri contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ? AND " + name;
        Cursor cursor = getActivity().getContentResolver().query(
                contentUri, projection, selection, null, null
        );

        try {

            if (cursor.moveToFirst()) {
                number = cursor.getString(0);
            }

        } finally {
            cursor.close();
        }

        // Check: Number is null
        if (number == null) {
            number = "Unsaved";
        }

        return number;
    }*/

    /**
     * Formats Crime's date to a user-friendly format and
     * sets the DateButton's text to this date
     */
    private void updateDate() {
        String formattedDate = formatDate(mCrime.getDate());
        mDateButton.setText(formattedDate);
    }

    /**
     * Formats Crime's time to a user-friendly format and
     * sets the DateButton's text to this date
     */
    private void updateTime() {
        String formattedDate = formatTime(mCrime.getDate());
        mTimeButton.setText(formattedDate);
    }

    /**
     * Helper method for editing Date Button text
     *
     */
    private void editDateButton() {
        // Retrieve FragmentManager
        FragmentManager fragmentManager = getFragmentManager();

        // Create a new instance of DatePickerFragment (AlertDialog)
        DatePickerFragment dialogDate = DatePickerFragment.newInstance(mCrime.getDate());

        // Set target fragment of DatePickerFragment -> CrimeFragment (for passing data)
        dialogDate.setTargetFragment(CrimeFragment.this, REQUEST_DATE);

        // Display the DatePickerFragment instance
        dialogDate.show(fragmentManager, DIALOG_DATE);
    }

    /**
     * Helper method for editing Time Button text
     *
     */
    private void editTimeButton() {
        // Retrieve FragmentManager
        FragmentManager fragmentManager = getFragmentManager();

        // Create a new instance of TimePickerFragment (AlertDialog)
        TimePickerFragment dialogTime = TimePickerFragment.newInstance(mCrime.getDate());

        // Set target fragment of TimePickerFragment -> CrimeFragment (for passing data)
        dialogTime.setTargetFragment(CrimeFragment.this, REQUEST_TIME);

        // Display the TimePickerFragment instance
        dialogTime.show(fragmentManager, DIALOG_TIME);
    }

    /**
     * Convert Date to String using specified format
     *
     * Ex: "Wednesday February 13, 2016"
     *
     * @param date
     * @return
     */
    public String formatDate(Date date) {
        String format = "EEEE MMM dd, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        return simpleDateFormat.format(date);
    }

    /**
     * Convert time to String using specified format
     *
     * Ex: "04:01 PM"
     *
     * @param time
     * @return
     */
    public String formatTime(Date time) {
        String format = "hh:mm aa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        return simpleDateFormat.format(time);
    }

}