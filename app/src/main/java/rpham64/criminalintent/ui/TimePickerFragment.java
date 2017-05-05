package rpham64.criminalintent.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rpham64.criminalintent.R;

/**
 * AlertDialog with TimePicker widget
 *
 * Created by Rudolf on 2/13/2016.
 */
public class TimePickerFragment extends DialogFragment implements TimePicker.OnTimeChangedListener {

    private static final String TAG = TimePickerFragment.class.getName();

    public interface Extras {
        String time = "TimePickerFragment.time";
    }

    public interface Arguments {
        String time = "ARG_TIME";
    }

    @BindView(R.id.dialog_time_picker) TimePicker mTimePicker;

    private Unbinder mUnbinder;
    private Calendar mCalendar;

    public static TimePickerFragment newInstance(Date time) {

        Bundle args = new Bundle();
        args.putSerializable(Arguments.time, time);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mUnbinder = ButterKnife.bind(this, view);

        mCalendar = Calendar.getInstance();
        Date time = (Date) getArguments().getSerializable(Arguments.time);

        mCalendar.setTime(time);

        mTimePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTimePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));

        mTimePicker.setOnTimeChangedListener(this);

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

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

        mTimePicker.setCurrentHour(hourOfDay);
        mTimePicker.setCurrentMinute(minute);

        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
    }

    /**
     * Send Date to target fragment CrimeFragment
     * Triggered when user clicks the AlertDialog's OK button
     *
     * @param resultCode
     */
    private void sendResult(int resultCode) {

        if (getTargetFragment() == null) return;

        Intent intent = new Intent();
        intent.putExtra(Extras.time, mCalendar.getTime());

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
