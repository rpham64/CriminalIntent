package bignerdranch.criminalintent.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import bignerdranch.criminalintent.BaseFragment;
import bignerdranch.criminalintent.R;
import bignerdranch.criminalintent.models.Crime;
import bignerdranch.criminalintent.models.CrimeLab;
import bignerdranch.criminalintent.utils.PictureUtils;
import bignerdranch.criminalintent.utils.ZoomImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import icepick.State;

import static bignerdranch.criminalintent.Permissions.REQUEST_CONTACT;
import static bignerdranch.criminalintent.Permissions.REQUEST_DATE;
import static bignerdranch.criminalintent.Permissions.REQUEST_PHOTO;
import static bignerdranch.criminalintent.Permissions.REQUEST_TIME;
import static bignerdranch.criminalintent.utils.TimeUtils.formatDate;
import static bignerdranch.criminalintent.utils.TimeUtils.formatTime;

/**
 * Created by Rudolf on 2/8/2016.
 */
public class CrimeFragment extends BaseFragment {

    private static final String TAG = CrimeFragment.class.getName();

    interface Extras {
        String crimeId = "CrimeFragment.crimeId";
        String contactNumber = "CrimeFragment.mContactNumber";
    }

    interface Tags {
        String dialogDate = "CrimeFragment.dialogDate";
        String dialogTime = "CrimeFragment.dialogTime";
    }

    @BindView(R.id.crime_title) EditText etxtTitle;
    @BindView(R.id.crime_photo) ImageView imgPhoto;
    @BindView(R.id.crime_camera) ImageButton btnCamera;
    @BindView(R.id.crime_date) Button btnDate;
    @BindView(R.id.crime_time) Button btnTime;
    @BindView(R.id.crime_solved) CheckBox chkBoxSolved;
    @BindView(R.id.crime_suspect) Button btnSuspectName;
    @BindView(R.id.crime_call_suspect) Button btnCallSuspect;
    @BindView(R.id.crime_report) Button btnReport;
    @BindView(R.id.crime_photo_expanded) ZoomImageView imgZoom;

    @State String mContactId;
    @State String mContactNumber;

    private Unbinder mUnbinder;
    private PackageManager mPackageManager;

    private Uri mContactUri;
    private UUID mCrimeId;
    private Crime mCrime;
    private File mPhotoFile;
    private Intent mCaptureImage;

    public static CrimeFragment newInstance(UUID crimeId) {

        Bundle args = new Bundle();
        args.putSerializable(Extras.crimeId, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mCrimeId = (UUID) getArguments().getSerializable(Extras.crimeId);
        }

        mCrime = CrimeLab.get(getActivity()).getCrime(mCrimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

        mPackageManager = getActivity().getPackageManager();
        mCaptureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        etxtTitle.setText(mCrime.getTitle());
        chkBoxSolved.setChecked(mCrime.isSolved());

        updateDate();
        updateTime();
        setPhoto();
        setPhotoButton();
        setSuspect();

        etxtTitle.addTextChangedListener(new TextWatcher() {
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

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Case: MenuItem is "Delete"
            case (R.id.menu_item_delete_crime):

                // Remove crime from CrimeLab
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                Toast.makeText(getActivity(), R.string.toast_crime_deleted, Toast.LENGTH_SHORT).show();

                getActivity().finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check: resultCode is OK or not
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {

            case (REQUEST_DATE):

                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);

                updateDate();

                break;

            case (REQUEST_TIME):

                Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                mCrime.setDate(time);

                updateTime();

                break;

            case (REQUEST_CONTACT):

                // Check: data is null
                if (data == null) return;

                mContactUri = data.getData();

                // Check the SDK version and whether the permission is already granted or not.
                // If version Marshmallow (6.0) or higher, request permission
                // Else, retrieve contact info (permission already granted)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);
                } else {
                    getContactName(mContactUri);
                    getContactNumber(mContactUri);
                }

                break;

            case (REQUEST_PHOTO):
                setPhotoButton();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CONTACT && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // Permission is granted
            getContactName(mContactUri);
            getContactNumber(mContactUri);

        } else {
            String getContactFailedMessage = getString(R.string.permissions_to_retrieve_contact_failed);
            Logger.d(getContactFailedMessage);
            Toast.makeText(getActivity(), getContactFailedMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDate() {
        String formattedDate = formatDate(mCrime.getDate());
        btnDate.setText(formattedDate);
    }

    private void updateTime() {
        String formattedDate = formatTime(mCrime.getDate());
        btnTime.setText(formattedDate);
    }

    private void setPhoto() {
        // If mPhotoFile exists, set imgPhoto to the image in mPhotoFile
        if (mPhotoFile == null || !mPhotoFile.exists()) {

            imgPhoto.setImageDrawable(null);

        } else {

            final Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());

            imgPhoto.setImageBitmap(bitmap);
            imgZoom.setImageBitmap(bitmap);
        }
    }

    private void setPhotoButton() {

        boolean canTakePhoto = mPhotoFile != null
                && mCaptureImage.resolveActivity(mPackageManager) != null;

        // Disable camera button if canTakePhoto is false. Else, set enabled.
        btnCamera.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            mCaptureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
    }

    private void setSuspect() {

        // Create Implicit Intent with action PICK and location Content URI
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        // Set text of Suspect Button to suspect, if it exists
        if (mCrime.getSuspect() != null) {
            btnSuspectName.setText(mCrime.getSuspect());
        }

        // If no contact app exists, disable Suspect button
        if (mPackageManager.resolveActivity(pickContact,
                mPackageManager.MATCH_DEFAULT_ONLY) == null) {
            btnSuspectName.setEnabled(false);
        }
    }

    private String getCrimeReport() {

        String crimeDate = formatDate(mCrime.getDate());
        String isSolved;
        String suspect;

        /** isSolved */
        if (mCrime.isSolved()) {
            isSolved = getString(R.string.crime_report_solved);
        } else {
            isSolved = getString(R.string.crime_report_unsolved);
        }

        /** suspect */
        suspect = mCrime.getSuspect();

        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        return getString(R.string.crime_report, mCrime.getTitle(),
                crimeDate, isSolved, suspect);
    }

    private void getContactName(Uri contactUri) {

        // Store field for Contact name
        String[] queryFields = new String[] {ContactsContract.Contacts.DISPLAY_NAME};

        // Perform query using mContactUri as "whereClause"
        Cursor cursor = getActivity().getContentResolver()
                .query(contactUri, queryFields, null, null, null);

        try {

            if (cursor.getCount() == 0) return;

            // Pull out suspect name data from first row, first column
            cursor.moveToFirst();
            String suspect = cursor.getString(0);
            mCrime.setSuspect(suspect);
            btnSuspectName.setText(suspect);

        } catch (NullPointerException npe) {
            npe.printStackTrace();
            Logger.e("Cursor in CrimeFragment is null.");

        } finally {
            cursor.close();                 // To avoid Cursor exceptions
        }

        Log.d(TAG, "Contact Name: " + mCrime.getSuspect());
    }

    private void getContactNumber(Uri contactUri) {

        String selection;
        String[] selectionArgs;

        // Store field for Contact number
        String[] queryFields = new String[] {ContactsContract.Contacts._ID};

        // Perform query using mContactUri as "whereClause"
        Cursor cursorID = getActivity().getContentResolver()
                .query(contactUri, queryFields, null, null, null);

        try {

            if (cursorID.moveToFirst()) {

                int columnIndex = cursorID.getColumnIndex(ContactsContract.Contacts._ID);
                mContactId = cursorID.getString(columnIndex);

            }

        } finally {
            cursorID.close();                 // To avoid Cursor exceptions
        }

        Log.d(TAG, "Contact ID: " + mContactId);

        // Use mContactId to retrieve contact phone number
        contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        queryFields = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER};
        selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        selectionArgs = new String[] {mContactId};

        Cursor cursorPhone = getActivity().getContentResolver().query(
                contactUri, queryFields, selection, selectionArgs, null);

        try {

            if (cursorPhone.moveToFirst()) {

                int columnIndex = cursorPhone.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER);
                mContactNumber = cursorPhone.getString(columnIndex);

            }

        } finally {
            cursorPhone.close();
        }

        Log.d(TAG, "Contact Phone Number: " + mContactNumber);

    }

    @OnClick(R.id.crime_photo)
    public void onPhotoClicked() {
        imgZoom.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.crime_photo_expanded)
    public void onPhotoZoomedClicked() {
        imgZoom.setVisibility(View.GONE);
    }

    @OnClick(R.id.crime_camera)
    public void onCameraClicked() {
        startActivityForResult(mCaptureImage, REQUEST_PHOTO);
    }

    @OnClick(R.id.crime_date)
    public void onDateClicked() {
        FragmentManager fragmentManager = getFragmentManager();
        DatePickerFragment dialogDate = DatePickerFragment.newInstance(mCrime.getDate());
        dialogDate.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
        dialogDate.show(fragmentManager, Tags.dialogDate);
    }

    @OnClick(R.id.crime_time)
    public void onTimeClicked() {
        FragmentManager fragmentManager = getFragmentManager();
        TimePickerFragment dialogTime = TimePickerFragment.newInstance(mCrime.getDate());
        dialogTime.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
        dialogTime.show(fragmentManager, Tags.dialogTime);
    }

    @OnClick(R.id.crime_solved)
    public void onSolvedChecked() {
        mCrime.setSolved(chkBoxSolved.isChecked());
    }

    @OnClick(R.id.crime_suspect)
    public void onSuspectClicked() {
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(pickContact, REQUEST_CONTACT);
    }

    @OnClick(R.id.crime_call_suspect)
    public void onCallSuspect() {

        // Dial contact number using phone app
        String uri = "tel:" + mContactNumber.trim();
        Uri number = Uri.parse(uri);
        Intent callNumber = new Intent(Intent.ACTION_DIAL, number);

        startActivity(callNumber);
    }

    @OnClick(R.id.crime_report)
    public void onReportClicked() {
        // Build Intent using ShareCompat.IntentBuilder
        ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(getActivity());

        intentBuilder.setChooserTitle(R.string.send_report)
                .setType("text/plain")
                .setSubject(getString(R.string.crime_report_subject))
                .setText(getCrimeReport());

        // Create chooser intent and send to OS
        Intent intent = Intent.createChooser(
                intentBuilder.getIntent(),
                getString(R.string.send_report));

        startActivity(intent);
    }
}