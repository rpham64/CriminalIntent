package bignerdranch.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Rudolf on 2/8/2016.
 */
public class CrimeFragment extends Fragment {

    // Fragment argument for crime_id
    private static final String ARG_CRIME_ID = "crime_id";

    // Tag for DatePickerFragment
    private static final String DIALOG_DATE = "DialogDate";

    // Request code for Date (sent by DatePickerFragment)
    private static final int REQUEST_DATE = 0;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the UUID from fragment arguments
        UUID crimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        // Fetch the Crime object using the UUID
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflates the layout "fragment_crime.xml"
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        // Get reference to mTitleField (inflate widget)
        mTitleField = (EditText) view.findViewById(R.id.crime_title);

        // Set Text to mTitleField
        mTitleField.setText(mCrime.getTitle());

        // Add a listener for mTitleField
        mTitleField.addTextChangedListener(new TextWatcher() {
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
        });

        // Get reference to mDateButton, set text to date of crime, and disable for now
        mDateButton = (Button) view.findViewById(R.id.crime_date);

        // Update DateButton's text
        updateDate();

        // Create listener for mDateButton
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

                // Retrieve FragmentManager
                FragmentManager fragmentManager = getFragmentManager();

                // Create a new instance of DatePickerFragment (AlertDialog)
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());

                // Set target fragment of DatePickerFragment -> CrimeFragment (for passing data)
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);

                // Display the DatePickerFragment instance
                dialog.show(fragmentManager, DIALOG_DATE);
            }
        });

        // Get reference and set listener to CheckBox for updating mSolvedCheckBox
        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);

        // Set Checked status
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        // Add a listener for mSolvedCheckBox
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Set crime's solved property
                mCrime.setSolved(isChecked);
            }
        });

        return view;
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

        // Check: resultCode is OK or not
        if (resultCode != Activity.RESULT_OK) return;

        // Check: requestCode is equal to REQUEST_DATE
        if (requestCode == REQUEST_DATE) {

            // If so, retrieve the extra (Date) from the Intent
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            // Set the Crime object's date to this Date
            mCrime.setDate(date);

            // Update DateButton's text
            updateDate();
        }

    }

    /**
     * Formats Crime's date to a user-friendly format and
     * sets the DateButton's text to this date
     */
    private void updateDate() {
        String formattedDate = formatDate(mCrime.getDate());
        mDateButton.setText(formattedDate);
    }

    /**
     * Convert Date to String using specified format
     *
     * Ex: "SATURDAY FEB 13, 2016 04:01:19 PM"
     *
     * @param date
     * @return
     */
    public String formatDate(Date date) {
        String format = "EEEE MMM dd, yyyy KK:mm:ss aa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        return simpleDateFormat.format(date);
    }

}