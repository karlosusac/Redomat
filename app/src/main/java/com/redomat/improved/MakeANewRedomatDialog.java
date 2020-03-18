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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.redomat.improved.databinding.DialogMakeANewRedomatBinding;


public class MakeANewRedomatDialog extends DialogFragment {

    //View binding
    private DialogMakeANewRedomatBinding diagNewRedBinding;

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
        diagNewRedBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_make_a_new_redomat, null, false);
        builder.setView(diagNewRedBinding.getRoot());

        //EditText
        makeANewRedomatInputName = diagNewRedBinding.newRedomatInputName;

        //Buttons
        makeANewRedomatBtnConfirm = diagNewRedBinding.newRedomatBtnConfirm;

        //Progress Bar
        progressBar = diagNewRedBinding.newRedomatProgressBar;
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

                validateRedomatLineName(makeANewRedomatInputName.getEditText().getText().toString());
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

    //Intercae that sends data required to make an new Redomat to the MainManuActivity
    public interface MakeANewRedomatDialogListener{
        void makeNewRedomat(String redomatName);
    }

    //Check if redomat name is valid, if it is not empty and/or longer than 25 characters
    private void validateRedomatLineName(String name){
        if(name.isEmpty()){
            makeANewRedomatInputName.setError(getString(R.string.makeANewRedomatEnterRedomatName));
            progressBar.setVisibility(View.GONE);
        } else  if(name.length() > 25){
            makeANewRedomatInputName.setError("Uneseno ime je predugo");
            progressBar.setVisibility(View.GONE);
        } else {
            listener.makeNewRedomat(makeANewRedomatInputName.getEditText().getText().toString());
            dismiss();
            progressBar.setVisibility(View.GONE);
        }
    }
}
