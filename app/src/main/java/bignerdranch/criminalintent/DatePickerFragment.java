package bignerdranch.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Rudolf on 2/12/2016.
 */
public class DatePickerFragment extends DialogFragment {

    // Fragment argument for Date
    private static final String ARG_DATE = "date";

    // Key for Date extra
    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";

    // DatePicker widget
    private DatePicker mDatePicker;

    /**
     * Creates a new instance of DatePickerFragment and
     * stores fragment arguments to access Intent extras
     *
     * @return
     */
    public static DatePickerFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create AlertDialog with DatePicker widget
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Retrieve Date from fragment arguments
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        // Create a Calendar and set the time to Date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Store year, month, and day as integers
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Inflate DatePicker view
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);

        // Set DatePicker's view using the stored year, month, day variables
        mDatePicker = (DatePicker) view.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year, month, day, null);

        // Return an instance of AlertDialog with the DatePicker widget
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    /**
                     * On click, sends Intent with Date extra to the target fragment
                     *
                     * @param dialog
                     * @param which
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();
    }

    /**
     * Send Date to TargetFragment (CrimeFragment)
     * Triggered when user clicks the AlertDialog's PositiveButton
     *
     * @param resultCode
     * @param date
     */
    private void sendResult(int resultCode, Date date) {

        // Check: TargetFragment exists or not
        if (getTargetFragment() == null) return;

        // Create a new intent with the date extra
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        // Send intent to target fragment using TargetFragment.onActivityResult
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}