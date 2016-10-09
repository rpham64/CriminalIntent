package bignerdranch.criminalintent.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import bignerdranch.criminalintent.R;

/**
 * NO UWTWF
 *
 * Created by Rudolf on 2/13/2016.
 */
public class TimePickerFragment extends DialogFragment {

    // TAG for TimePickerFragment
    private static final String TAG = "TimePickerFragment";

    // Key for Time extra
    public static final String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";

    // Fragment argument for Time
    private static final String ARG_TIME = "time";

    // TimePicker widget
    private TimePicker mTimePicker;

    // Time
    private Date mTime;

    /**
     * Creates a new instance of TimePickerFragment and
     * stores fragment arguments for access to Intent extras
     *
     * @return
     */
    public static TimePickerFragment newInstance(Date time) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, time);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create AlertDialog with TimePicker widget
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Retrieve Date from fragment arguments
        Date time = (Date) getArguments().getSerializable(ARG_TIME);

        // Create a Calendar and set the time to Date
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);

        Log.d(TAG, "Current Time: " + time);

        // Inflate TimePicker view
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);

        // Set TimePicker's view using the stored hour and minute variables
        mTimePicker = (TimePicker) view.findViewById(R.id.dialog_time_time_picker);
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                // Set calendar time to new time
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                Log.d(TAG, "Hour: " + hourOfDay);
                Log.d(TAG, "Minute: " + minute);

                mTime = calendar.getTime();

                Log.d(TAG, "New Time: " + mTime);

            }
        });

        // Default mTime
        // Fixes bug of opening dialog and not picking new time
        mTime = calendar.getTime();

        Log.d(TAG, "Outside Time: " + mTime);

        // Store updated mTime in argument
        getArguments().putSerializable(ARG_TIME, mTime);

        // Return an instance of AlertDialog with the TimePicker widget
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    /**
                     * On click, sends Intent with Date extra to the target fragment
                     *
                     * @param dialog
                     * @param which
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    /**
     * Send Date to target fragment CrimeFragment)
     * Triggered when user clicks the AlertDialog's PositiveButton
     *
     * @param resultCode
     */
    private void sendResult(int resultCode) {

        // Check: TargetFragment exists or not
        if (getTargetFragment() == null) return;

        // Create a new intent with the date extra
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, mTime);

        // Send intent to target fragment using TargetFragment.onActivityResult
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
