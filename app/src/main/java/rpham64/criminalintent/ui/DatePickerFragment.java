package bignerdranch.criminalintent.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import bignerdranch.criminalintent.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * An AlertDialog with DatePicker widget
 *
 * Created by Rudolf on 2/12/2016.
 */
public class DatePickerFragment extends DialogFragment {

    private static final String TAG = DatePickerFragment.class.getName();

    public interface Extras {
        String date = "DatePickerFragment.date";
    }

    interface Arguments {
        String date = "ARG_DATE";
    }

    @BindView(R.id.dialog_date_picker) DatePicker mDatePicker;

    private Unbinder mUnbinder;
    private Date mDate;

    public static DatePickerFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(Arguments.date, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);
        mUnbinder = ButterKnife.bind(this, view);

        if (getArguments() != null) {
            mDate = (Date) getArguments().getSerializable(Arguments.date);
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);

        // Store current year, month, and day as integers
        int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Set DatePicker's view using the stored year, month, day variables
        mDatePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                // Set calendar date to new date
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                mDate = calendar.getTime();
            }
        });

        // Stores default calendar date in mDate
        // Fixes bug of opening dialog and not choosing a new date
        mDate = calendar.getTime();

        getArguments().putSerializable(Arguments.date, mDate);

        // Return an instance of AlertDialog with the DatePicker widget
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.date_picker_title)
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
     * Send Date to TargetFragment (CrimeFragment)
     * Triggered when user clicks the AlertDialog's PositiveButton
     *
     * @param resultCode
     */
    private void sendResult(int resultCode) {

        if (getTargetFragment() == null) return;

        Intent intent = new Intent();
        intent.putExtra(Extras.date, mDate);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
