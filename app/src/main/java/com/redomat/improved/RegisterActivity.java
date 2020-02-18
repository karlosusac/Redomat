package com.redomat.improved;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.redomat.improved.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    //Firebase stuff
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase db = FirebaseDatabase.getInstance();

    //View Binding
    private ActivityRegisterBinding mBiding;
    //--------------------------------------

    //Initializing activity variables
    private TextInputLayout regInputEmail;
    //--------------------------------

    //Progress dialog
    ProgressDialog progressDialog;
    //-------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBiding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View registerView = mBiding.getRoot();
        setContentView(registerView);
    }

    //Function to go back to Login Acitivity - Used in XML as a link
    public void openLoginActivity(View v){
        showProgressDialog();
        finish();
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
}
