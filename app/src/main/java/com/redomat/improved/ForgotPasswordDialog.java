package com.redomat.improved;

import android.app.Dialog;
import android.content.DialogInterface;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class ForgotPasswordDialog extends DialogFragment {

    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //Initializing activity variables
    private TextInputLayout frgtPassDialogInputEmail;

    private Button frgtPassDialogBtnConfirm;
    //--------------------------------

    //Progress bar for the ForgotPasswordDialog - Different usage than ProgressBar class
    ProgressBar progressBar;


    //CODE
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_forgot_password, null);

        builder.setView(view);

        //EditText
        frgtPassDialogInputEmail = view.findViewById(R.id.frgtPassDialogInputEmail);

        //Buttons
        frgtPassDialogBtnConfirm = view.findViewById(R.id.frgtPassDialogBtnConfirm);

        //Progress Bar
        progressBar = view.findViewById(R.id.frgtPassDialogProgressBar);
        progressBar.setVisibility(View.GONE);

        builder.setTitle(getString(R.string.frgtPassDialogForgottenPassword))
                .setMessage(getString(R.string.frgtPassDialogMessage))
        .setNegativeButton(getString(R.string.frgtPassDialogCancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        frgtPassDialogBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if(validateEmail(String.valueOf(frgtPassDialogInputEmail.getEditText().getText()).trim())){
                    //check email already exist or not.
                    mAuth.fetchSignInMethodsForEmail(String.valueOf(frgtPassDialogInputEmail.getEditText().getText()).trim())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                                    if (isNewUser) {
                                        frgtPassDialogInputEmail.setError(getString(R.string.frgtPassEmailNotRegistered));
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        frgtPassDialogInputEmail.setError(null);
                                        mAuth.sendPasswordResetEmail(String.valueOf(frgtPassDialogInputEmail.getEditText().getText()).trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    dismiss();
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(getContext(), getString(R.string.frgtPassResetPasswordEmailSent), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.d("ForgotPassword", task.getException().toString());
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }

                                }
                            });
                }
            }
        });

        return builder.create();
    }

    //CUSTOM METHODS
    //Check if email is entered and if so, check if it is entered correctly and return the output
    public boolean validateEmail(String email){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(email.isEmpty()) {
            frgtPassDialogInputEmail.setError(getString(R.string.frgtPassInputErrorEmail));
            progressBar.setVisibility(View.GONE);
            return false;
        }else {
            if (!email.trim().matches(emailPattern)) {
                frgtPassDialogInputEmail.setError(getString(R.string.frgtPassInputErrorEmailValid));
                progressBar.setVisibility(View.GONE);
                return false;
            } else {
                frgtPassDialogInputEmail.setError(null);
                return true;
            }
        }
    }
}
