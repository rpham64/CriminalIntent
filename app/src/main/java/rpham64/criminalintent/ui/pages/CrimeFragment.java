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
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
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
import rpham64.criminalintent.BuildConfig;
import rpham64.criminalintent.R;
import rpham64.criminalintent.models.Crime;
import rpham64.criminalintent.models.database.CrimeLab;
import rpham64.criminalintent.ui.BaseFragment;
import rpham64.criminalintent.ui.DatePickerFragment;
import rpham64.criminalintent.ui.TimePickerFragment;
import rpham64.criminalintent.ui.imageView.PhotoViewActivity;
import rpham64.criminalintent.utils.ContactUtils;
import rpham64.criminalintent.utils.IntentUtils;

import static rpham64.criminalintent.utils.Permissions.REQUEST_CONTACT;
import static rpham64.criminalintent.utils.Permissions.REQUEST_DATE;
import static rpham64.criminalintent.utils.Permissions.REQUEST_PHOTO;
import static rpham64.criminalintent.utils.Permissions.REQUEST_TIME;

/**
 * Created by Rudolf on 2/8/2016.
 */
public class CrimeFragment extends BaseFragment implements TextWatcher, CrimePresenter.View {

    public static final String TAG = CrimeFragment.class.getName();

    private static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    interface Extras {
        String crimeId = "CrimeFragment.crimeId";
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
    private CrimePresenter mPresenter;

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

        UUID crimeId = null;

        if (getArguments() != null) {
            crimeId = (UUID) getArguments().getSerializable(Extras.crimeId);
        }

        Crime crime = CrimeLab.get(getActivity()).getCrime(crimeId);
        File photoFile = CrimeLab.get(getActivity()).getPhotoFile(getActivity(), crime.getPhotoFilename());

        mPresenter = new CrimePresenter(crime);
        mPresenter.setPhotoFile(photoFile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_fragment_crime, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mPresenter.attachView(this);

        etxtTitle.addTextChangedListener(this);

        mPresenter.setPhotoView();
        mPresenter.updateTitle();
        mPresenter.updateDate();
        mPresenter.updateTime();
        mPresenter.setSolvedCheckBox();
        mPresenter.setButtonSuspect();
        mPresenter.setCallButtonEnabled();

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

            case (R.id.menu_item_delete_crime):

                mPresenter.deleteCrime();
                getActivity().finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.updateCrime();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
        mUnbinder.unbind();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_CONTACT:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission granted to retrieve contact
                    IntentUtils.pickContact(this);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {

            case (REQUEST_DATE):

                Date date = (Date) data.getSerializableExtra(DatePickerFragment.Extras.date);
                mPresenter.setDate(date);

                break;

            case (REQUEST_TIME):

                Date time = (Date) data.getSerializableExtra(TimePickerFragment.Extras.time);
                mPresenter.setTime(time);

                break;

            case (REQUEST_CONTACT):

                // Check: data is null
                if (data == null) return;

                Uri contactUri = data.getData();

                String suspect = ContactUtils.getContactName(getContext(), contactUri);
                String number = ContactUtils.getContactNumber(getContext(), contactUri);

                mPresenter.setSuspectInfo(suspect, number);

                break;

            case (REQUEST_PHOTO):

                mPresenter.setPhotoView();

        }

    }

    @Override
    public void deleteCrime(Crime crime) {
        CrimeLab.get(getActivity()).deleteCrime(crime);
        Toast.makeText(getActivity(), R.string.toast_crime_deleted, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateCrime(Crime crime) {
        CrimeLab.get(getActivity()).updateCrime(crime);
    }

    @Override
    public void setPhoto(File photo) {
        Picasso.with(getActivity())
                .load(photo)
                .fit()
                .centerCrop()
                .placeholder(null)
                .into(imgPhoto);
    }

    @Override
    public void updateEditTextTitle(String title) {
        etxtTitle.setText(title);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mPresenter.setTitle(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void startCamera() {

        File photoFile = mPresenter.getPhotoFile();

        // Invalidate cache for old image (overwriting)
        Picasso.with(getActivity()).invalidate(photoFile);

        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = photoFile != null &&
                cameraIntent.resolveActivity(getActivity().getPackageManager()) != null;

        if (canTakePhoto) {

            Uri uri;

            // Android api level 24+ (Nougat) requires FileProvider for creating a Uri
            // For levels 23 and below, use Uri.fromFile
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(getContext(), AUTHORITY, photoFile);
            } else {
                uri = Uri.fromFile(photoFile);
            }

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            startActivityForResult(cameraIntent, REQUEST_PHOTO);
        }
    }

    @Override
    public void showDateDialog(Date date) {
        FragmentManager fragmentManager = getFragmentManager();
        DatePickerFragment dialogDate = DatePickerFragment.newInstance(date);
        dialogDate.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
        dialogDate.show(fragmentManager, Tags.dialogDate);
    }

    @Override
    public void showTimeDialog(Date time) {
        FragmentManager fragmentManager = getFragmentManager();
        TimePickerFragment dialogTime = TimePickerFragment.newInstance(time);
        dialogTime.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
        dialogTime.show(fragmentManager, Tags.dialogTime);
    }

    @Override
    public void updateButtonDate(String date) {
        btnDate.setText(date);
    }

    @Override
    public void updateButtonTime(String time) {
        btnTime.setText(time);
    }

    @Override
    public void setSolvedCheckBox(boolean isSolved) {
        chkBoxSolved.setChecked(isSolved);
    }

    @Override
    public void findSuspect() {

        // Request permission to search contacts
        // Else, if already granted, launch contacts application
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);

        } else {
            IntentUtils.pickContact(this);
        }

    }

    @Override
    public void updateButtonSuspect(String suspect) {

        // Create Implicit Intent with action PICK and location Content URI
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        // If no contact app exists, disable Suspect button
        PackageManager packageManager = getActivity().getPackageManager();

        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            btnSuspectName.setEnabled(false);
        }

        if (suspect != null) btnSuspectName.setText(suspect);
    }

    @Override
    public void setCallButtonEnabled(boolean isEnabled) {
        btnCallSuspect.setEnabled(isEnabled);
    }

    @Override
    public void startCall(String phoneNumber) {
        // Dial contact number using phone app
        String uri = "tel:" + phoneNumber.trim();
        Uri number = Uri.parse(uri);
        Intent callNumber = new Intent(Intent.ACTION_DIAL, number);

        startActivity(callNumber);
    }

    @Override
    public void sendCrimeReport() {

        String crimeReport = mPresenter.getCrimeReport();

        Intent crimeReportIntent = IntentUtils.buildCrimeReportIntent(getActivity(), crimeReport);
        startActivity(crimeReportIntent);
    }

    @Override
    public String buildCrimeReport(String title, String date, boolean isSolved, boolean hasSuspect, String suspect) {

        String solvedText = isSolved ?
                getString(R.string.crime_report_solved) :
                getString(R.string.crime_report_unsolved);

        String suspectText = hasSuspect ?
                getString(R.string.crime_report_no_suspect) :
                getString(R.string.crime_report_suspect, suspect);

        return getString(R.string.crime_report, title, date, solvedText, suspectText);
    }

    @OnClick(R.id.crime_photo)
    public void onPhotoClicked() {
        if (Build.VERSION.SDK_INT < 21) {
            Toast.makeText(getContext(), "21+ only, keep out", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getContext(), PhotoViewActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(getActivity(), imgPhoto, getString(R.string.view_photo_in_full_screen));
            startActivity(intent, options.toBundle());
        }
    }

    @OnClick(R.id.crime_camera)
    public void onCameraClicked() {
        // If permission not granted, request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PHOTO);

        } else {

            // Permission already granted, so start camera
            mPresenter.onCameraButtonClicked();
        }
    }

    @OnClick(R.id.crime_date)
    public void onDateClicked() {
        mPresenter.onDateButtonClicked();
    }

    @OnClick(R.id.crime_time)
    public void onTimeClicked() {
        mPresenter.onTimeButtonClicked();
    }

    @OnClick(R.id.crime_solved)
    public void onSolvedButtonChecked() {
        mPresenter.onSolvedButtonChecked();
    }

    @OnClick(R.id.crime_suspect)
    public void onSuspectButtonClicked() {
        mPresenter.onSuspectButtonClicked();
    }

    @OnClick(R.id.crime_call_suspect)
    public void onCallSuspect() {
        mPresenter.onCallButtonClicked();
    }

    @OnClick(R.id.crime_report)
    public void onReportClicked() {
        mPresenter.onSendCrimeReportButtonClicked();
    }
}