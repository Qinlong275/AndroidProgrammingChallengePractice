package com.bignerdranch.android.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import static com.bignerdranch.android.criminalintent.PictureUtils.getScaledBitmap;

/**
 * Created by 秦龙 on 2017/9/18.
 */

public class CrimePhotoDialogFragment extends DialogFragment {

    private static final String ARG_IMAGE = "image";

    private ImageView mImageZoom;

    public static CrimePhotoDialogFragment newInstance(String imageFile) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_IMAGE, imageFile);

        CrimePhotoDialogFragment fragment = new CrimePhotoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String imageFile = (String) getArguments().getSerializable(ARG_IMAGE);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);

        Bitmap bitmap = getScaledBitmap(imageFile, getActivity());

        mImageZoom = (ImageView) v.findViewById(R.id.dialog_photo);
        mImageZoom.setImageBitmap(bitmap);
        mImageZoom.setOnClickListener(new View.OnClickListener() {//点击后退出dialog
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }
}
