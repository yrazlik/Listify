package com.yrazlik.listify;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by yrazlik on 9/4/15.
 */
public class CustomDialogFragment extends DialogFragment implements TextView.OnEditorActionListener
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Set a theme on the dialog builder constructor!
        AlertDialog.Builder builder =
                new AlertDialog.Builder( getActivity(), R.style.MyCustomTheme );

        builder
                .setTitle( "Your title" )
                .setMessage( "Your message" )
                .setPositiveButton( "OK" , new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }
}