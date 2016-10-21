package rpham64.criminalintent.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import rpham64.criminalintent.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * AlertDialog with TimePicker widget
 *
 * Created by Rudolf on 2/13/2016.
 */
public class TimePickerFragment extends DialogFragment {

    private static final String TAG = TimePickerFragment.class.getName();

    public interface Extras {
        String time = "TimePickerFragment.time";
    }

    public interface Arguments {
        String time = "ARG_TIME";
    }

    @BindView(R.id.dialog_time_picker) TimePicker mTimePicker;

    private Unbinder mUnbinder;
    private Date mTime;

    public static TimePickerFragment newInstance(Date time) {

        Bundle args = new Bundle();
        args.putSerializable(Arguments.time, time);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);
        mUnbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            mTime = (Date) getArguments().getSerializable(Arguments.time);
        }

        // Create a Calendar and set the time to Date
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(mTime);

        // Set TimePicker's view using the stored hour and minute variables
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                // Set calendar time to new time
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                mTime = calendar.getTime();
            }
        });

        // Default mTime
        mTime = calendar.getTime();

        // Store updated mTime in argument
        getArguments().putSerializable(Arguments.time, mTime);

        // Return an instance of AlertDialog with the TimePicker widget
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> sendResult(Activity.RESULT_OK))
                .create();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    /**
     * Send Date to target fragment CrimeFragment)
     * Triggered when user clicks the AlertDialog's PositiveButton
     *
     * @param resultCode
     */
    private void sendResult(int resultCode) {

        if (getTargetFragment() == null) return;

        Intent intent = new Intent();
        intent.putExtra(Extras.time, mTime);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
