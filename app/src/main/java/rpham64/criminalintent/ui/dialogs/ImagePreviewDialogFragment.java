package rpham64.criminalintent.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;
import rpham64.criminalintent.R;

/**
 * Created by Rudolf on 5/5/2017.
 */

public class ImagePreviewDialogFragment extends SupportBlurDialogFragment {

    public interface Arguments {
        String IMAGE = ImagePreviewDialogFragment.class.getName() + ".image";
    }

    @BindView(R.id.view_image_preview) ImageView imgViewPhoto;

    private Unbinder mUnbinder;

    public static ImagePreviewDialogFragment newInstance(File image) {

        Bundle args = new Bundle();
        args.putSerializable(Arguments.IMAGE, image);

        ImagePreviewDialogFragment fragment = new ImagePreviewDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.view_image_preview, null);
        mUnbinder = ButterKnife.bind(this, view);

        File image = (File) getArguments().getSerializable(Arguments.IMAGE);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();

        // Removes dialog white borders
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Picasso.with(getActivity()).load(image).into(imgViewPhoto);

        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.view_image_preview)
    public void onPhotoClicked() {
        dismiss();
    }
}
