package com.example.imagefinder.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.imagefinder.R;

/**
 * Fragment for showing errors.
 */
public class ErrorDialogFragment extends DialogFragment {

    private static final String ARG_ERROR_MESSAGE = "args_error";

    public static ErrorDialogFragment newInstance(String errorMessage) {
        ErrorDialogFragment fragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String errorMessage = getArguments().getString(ARG_ERROR_MESSAGE);
        return new AlertDialog.Builder(getActivity())
                .setMessage(errorMessage)
                .setNeutralButton(R.string.ok, null)
                .create();
    }
}
