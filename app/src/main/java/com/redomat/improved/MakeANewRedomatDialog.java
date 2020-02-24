package com.redomat.improved;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;


public class MakeANewRedomatDialog extends DialogFragment {

    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //------------------------------------------------------

    //Initializing activity variables
    private TextInputLayout makeANewRedomatInputName;

    private Button makeANewRedomatBtnConfirm;
    //------------------------------

    //Progress bar for the MakeANewRedomatDialog - Different usage than ProgressBar class
    private ProgressBar progressBar;

    //Dialog listener
    private MakeANewRedomatDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_make_a_new_redomat, null);

        builder.setView(view);

        //EditText
        makeANewRedomatInputName = view.findViewById(R.id.newRedomatInputName);

        //Buttons
        makeANewRedomatBtnConfirm = view.findViewById(R.id.newRedomatBtnConfirm);

        //Progress Bar
        progressBar = view.findViewById(R.id.newRedomatProgressBar);
        progressBar.setVisibility(View.GONE);

        //CODE
        builder.setTitle(getString(R.string.makeANewRedomatTitle))
                .setNegativeButton(getString(R.string.makeANewRedomatCancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        makeANewRedomatBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                if(makeANewRedomatInputName.getEditText().getText().toString().isEmpty()){
                    makeANewRedomatInputName.setError(getString(R.string.makeANewRedomatEnterRedomatName));
                    progressBar.setVisibility(View.GONE);
                } else {
                    /*
                    Intent i = new Intent(getContext(), RedomatAdmin.class);
                    startActivity(i);
                     */

                    listener.sendRedomatName(makeANewRedomatInputName.getEditText().getText().toString());
                    dismiss();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (MakeANewRedomatDialogListener) context;
        } catch (ClassCastException e) {
            Toast.makeText(context, "Došlo je do pogreške", Toast.LENGTH_SHORT).show();
        }
    }

    public interface MakeANewRedomatDialogListener{
        void sendRedomatName(String redomatName);
    }
}
