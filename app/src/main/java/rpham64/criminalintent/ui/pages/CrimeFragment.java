package rpham64.criminalintent.ui.pages;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import icepick.State;
import rpham64.criminalintent.BaseFragment;
import rpham64.criminalintent.BuildConfig;
import rpham64.criminalintent.R;
import rpham64.criminalintent.models.Crime;
import rpham64.criminalintent.models.CrimeLab;
import rpham64.criminalintent.ui.DatePickerFragment;
import rpham64.criminalintent.ui.TimePickerFragment;
import rpham64.criminalintent.utils.ContactUtils;

import static rpham64.criminalintent.Permissions.REQUEST_CONTACT;
import static rpham64.criminalintent.Permissions.REQUEST_DATE;
import static rpham64.criminalintent.Permissions.REQUEST_PHOTO;
import static rpham64.criminalintent.Permissions.REQUEST_TIME;
import static rpham64.criminalintent.utils.TimeUtils.formatDate;
import static rpham64.criminalintent.utils.TimeUtils.formatTime;

/**
 * Created by Rudolf on 2/8/2016.
 */
public class CrimeFragment extends BaseFragment implements TextWatcher {

    public static final String TAG = CrimeFragment.class.getName();

    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

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

    private Unbinder mUnbinder;
    private PackageManager mPackageManager;

    @State String mSuspectName;
    @State String mSuspectNumber;

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
        View view = inflater.inflate(R.layout.view_fragment_crime, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        etxtTitle.setText(mCrime.getTitle());
        etxtTitle.addTextChangedListener(this);

        chkBoxSolved.setChecked(mCrime.isSolved());
        btnCallSuspect.setEnabled(mSuspectNumber != null);

        updateDate();
        updateTime();
        setPhoto();
        setSuspect();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check: resultCode is OK or not
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {

            case (REQUEST_DATE):

                Date date = (Date) data.getSerializableExtra(DatePickerFragment.Extras.date);
                mCrime.setDate(date);

                updateDate();

                break;

            case (REQUEST_TIME):

                Date time = (Date) data.getSerializableExtra(TimePickerFragment.Extras.time);
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
                    setSuspectNameAndNumber();
                }

                break;

            case (REQUEST_PHOTO):
                setPhoto();

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_CONTACT:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission granted to retrieve contact
                    setSuspectNameAndNumber();
                } else {
                    String getContactFailedMessage = getString(R.string.permissions_to_retrieve_contact_failed);
                    Logger.d(getContactFailedMessage);
                    Toast.makeText(getActivity(), getContactFailedMessage, Toast.LENGTH_SHORT).show();
                }

                break;

            case REQUEST_PHOTO:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission granted to use camera
                    startCamera();

                } else {
                    Toast.makeText(getActivity(), R.string.permissions_camera_must_be_granted, Toast.LENGTH_SHORT).show();
                }

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mCrime.setTitle(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void updateDate() {
        String date = formatDate(mCrime.getDate());
        btnDate.setText(date);
    }

    private void updateTime() {
        String time = formatTime(mCrime.getDate());
        btnTime.setText(time);
    }

    private void setPhoto() {

        if (mPhotoFile != null) {
            Picasso.with(getActivity())
                    .load(new File(mPhotoFile.getPath()))
                    .fit()
                    .centerCrop()
                    .placeholder(null)
                    .into(imgPhoto);
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

    private void setSuspectNameAndNumber() {
        mSuspectName = ContactUtils.getContactName(getContext(), mContactUri);
        mSuspectNumber = ContactUtils.getContactNumber(getContext(), mContactUri);

        mCrime.setSuspect(mSuspectName);
        btnSuspectName.setText(mSuspectName);

        // If no phone number, disable call button
        btnCallSuspect.setEnabled(mSuspectNumber != null);
    }

    private String getCrimeReport() {

        String crimeDate = formatDate(mCrime.getDate());

        String isSolved = mCrime.isSolved() ?
                getString(R.string.crime_report_solved) :
                getString(R.string.crime_report_unsolved);

        String suspect = mCrime.getSuspect() == null ?
                getString(R.string.crime_report_no_suspect) :
                getString(R.string.crime_report_suspect, mCrime.getSuspect());

        return getString(R.string.crime_report, mCrime.getTitle(),
                crimeDate, isSolved, suspect);
    }

    private void startCamera() {

        boolean canTakePhoto = mPhotoFile != null
                && mCaptureImage.resolveActivity(mPackageManager) != null;

        if (canTakePhoto) {

            Uri uri = FileProvider.getUriForFile(getContext(), AUTHORITY, mPhotoFile);
            mCaptureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            // For api levels 21 and up (lollipop+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mCaptureImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            startActivityForResult(mCaptureImage, REQUEST_PHOTO);
        }
    }

    @OnClick(R.id.crime_photo)
    public void onPhotoClicked() {
//        zoomImageFromThumb(imgPhoto);
    }

    @OnClick(R.id.crime_camera)
    public void onCameraClicked() {
        // If permission not granted, request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PHOTO);

        } else {

            // Permission already granted, so start camera
            startCamera();
        }
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
        String uri = "tel:" + mSuspectNumber.trim();
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