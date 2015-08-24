package com.navruz.parser.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;

import com.navruz.parser.R;

import org.androidannotations.annotations.EBean;

@EBean
public class ProgressDialogFragment extends DialogFragment {

    private String messageText = "";

    public ProgressDialogFragment() {
        this.setCancelable(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (messageText == null || messageText.isEmpty())
            messageText = getActivity().getResources().getString(R.string.please_wait);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage(messageText);
        dialog.setCancelable(false);
        messageText = null;
        return dialog;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}
