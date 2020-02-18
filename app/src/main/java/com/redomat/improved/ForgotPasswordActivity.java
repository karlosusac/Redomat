package com.redomat.improved;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;
import com.redomat.improved.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {
    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    //Initializing activity variables
    private TextInputLayout frgtPassInputEmail;

    private Button frgtPassBtnConfirm;
    //--------------------------------

    //Progress dialog
    ProgressDialog progressDialog;
    //-------------

    //View Binding
    private ActivityForgotPasswordBinding mBiding;
    //---------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBiding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View forgotPasswordView = mBiding.getRoot();
        setContentView(forgotPasswordView);

        //EditText
        frgtPassInputEmail = mBiding.frgtPassInputEmail;

        //Buttons
        frgtPassBtnConfirm = mBiding.frgtPassBtnConfirm;


        //MAIN
        frgtPassBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();

                if(validateEmail(String.valueOf(frgtPassInputEmail.getEditText().getText()).trim())){
                    //check email already exist or not.
                    mAuth.fetchSignInMethodsForEmail(String.valueOf(frgtPassInputEmail.getEditText().getText()).trim())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                                    boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                                    if (isNewUser) {
                                        closeProgressDialog();
                                        frgtPassInputEmail.setError(getString(R.string.frgtPassEmailNotRegistered));
                                    } else {
                                        frgtPassInputEmail.setError(null);
                                        mAuth.sendPasswordResetEmail(String.valueOf(frgtPassInputEmail.getEditText().getText()).trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.frgtPassResetPasswordEmailSent), Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Log.d("ForgotPassword", task.getException().toString());
                                                }
                                            }
                                        });
                                    }

                                }
                            });
                } else {
                    closeProgressDialog();
                }
            }
        });

    }

    //CUSTOM
    //Check if email is entered and if so, check if it is entered correctly and return the output
    public boolean validateEmail(String email){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(email.isEmpty()) {
            frgtPassInputEmail.setError(getString(R.string.frgtPassInputErrorEmail));
            return false;
        }else {
            if (!email.trim().matches(emailPattern)) {
                frgtPassInputEmail.setError(getString(R.string.frgtPassInputErrorEmailValid));
                return false;
            } else {
                frgtPassInputEmail.setError(null);
                return true;
            }
        }
    }

    //Make new Progress dialog for loading screen
    public void showProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }

    //Close progress dialog
    public void closeProgressDialog(){
        progressDialog.dismiss();
    }
}
