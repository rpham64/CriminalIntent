package bignerdranch.criminalintent;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Displays a zoomed-in ImageView on click
 *
 * Created by Rudolf on 3/7/2016.
 */
public class ZoomImageFragment extends DialogFragment implements View.OnClickListener {

    // Fragment argument for File
    private static final String ARG_FILE = "file";
    private static Serializable mPhotoFile;

    // Photo View
    private ImageView mPhotoView;

    // Photo Location
//    private File mPhotoFile;

    public static ZoomImageFragment newInstance() {

        Bundle args = new Bundle();
        args.putSerializable(ARG_FILE, mPhotoFile);

        ZoomImageFragment fragment = new ZoomImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Inflate DatePicker view
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.full_screen_imageview, null);

//        mPhotoView = (ImageView) view.findViewById(R.id.crime_photo);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public void onClick(View v) {
        getActivity().finish();
    }
}
